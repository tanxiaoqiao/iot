package com.honeywell.fireiot.security;

import com.honeywell.fireiot.security.access.AuthenticatedVoter;
import com.honeywell.fireiot.security.access.Http401AuthenticationEntryPoint;
import com.honeywell.fireiot.security.access.ResourceAccessDeniedHandler;
import com.honeywell.fireiot.security.login.DefaultUserDetailsServiceImpl;
import com.honeywell.fireiot.security.access.ResourceAccessDecisionManager;
import com.honeywell.fireiot.security.access.SecurityMetadataSource;
import com.honeywell.fireiot.security.login.LoginFailureHandler;
import com.honeywell.fireiot.security.login.LoginSuccessHandler;
import com.honeywell.fireiot.security.logout.LogoutSuccessHandler;
import com.honeywell.fireiot.sso.SSOFilter;
import com.honeywell.fireiot.sso.SSORestApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security核心配置类
 *
 * @Author: zhenzhong.wang
 * @Date: 9/4/2018 1:30 PM
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.login.processUrl}")
    private String loginProcessUrl;
    @Value("${security.logout.processUrl}")
    private String logoutProcessUrl;

    public static String anonymousUrl;
    public static String ignoreUrl;
    public static String authToken;

    @Value("${security.anonymousUrl}")
    public void setAnonymousUrl(String anonymousUrl) {
        WebSecurityConfig.anonymousUrl = anonymousUrl;
    }

    @Value("${security.ignoreUrl}")
    public void setIgnoreUrl(String ignoreUrl) {
        WebSecurityConfig.ignoreUrl = ignoreUrl;
    }

    @Value("${security.authToken}")
    public void setAuthToken(String authToken) {
        WebSecurityConfig.authToken = authToken;
    }

    @Bean
    public SecurityMetadataSource getSecurityMetadataSource() {
        return new SecurityMetadataSource();
    }

    /**
     * 403校验
     **/
    @Bean
    public ResourceAccessDecisionManager getResourceAccessDecisionManager() {
        ResourceAccessDecisionManager resourceAccessDecisionManager = new ResourceAccessDecisionManager();
        return resourceAccessDecisionManager;
    }

    @Bean
    public UserDetailsService defaultUserDetailsService() {
        return new DefaultUserDetailsServiceImpl();
    }

    /**
     * 自定义API安全过滤器
     *
     * @return
     */
    @Bean
    public FilterSecurityInterceptor resourceFilterSecurityInterceptor() {
        FilterSecurityInterceptor filterSecurityInterceptor = new FilterSecurityInterceptor();
        // 设定每次请求都检查
        filterSecurityInterceptor.setObserveOncePerRequest(false);
        // 访问决策管理器
        filterSecurityInterceptor.setAccessDecisionManager(getResourceAccessDecisionManager());
        // resource数据源，服务启动时获取权限-资源关系
        filterSecurityInterceptor.setSecurityMetadataSource(getSecurityMetadataSource());
        return filterSecurityInterceptor;
    }

    @Bean("sessionRegistry")
    public SessionRegistry getSessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AffirmativeBased getVoters() {
        List voterList = Arrays.asList(new AuthenticatedVoter());
        return new AffirmativeBased(voterList);
    }

    /**
     * 登出filter
     *
     * @return
     */
    public LogoutFilter getLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(new LogoutSuccessHandler(), new SecurityContextLogoutHandler());
        return logoutFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**--------------  SSO启动时加载SSOFilter，关闭时加载Spring Security的登录登出  -----------**/
        if (SSORestApi.ssoEnable) {
            http.formLogin().disable();
            http.addFilterBefore(new SSOFilter(), ChannelProcessingFilter.class);
        } else {
            http.formLogin()
                    .failureHandler(new LoginFailureHandler())
                    .successHandler(new LoginSuccessHandler())
                    .loginProcessingUrl(loginProcessUrl)
                    .usernameParameter("username")
                    .passwordParameter("password");
        }

        /**-------------  设置401校验投票器  -----------**/
        http.authorizeRequests()
                .accessDecisionManager(getVoters())
                .antMatchers(anonymousUrl.split(",")).access("IS_AUTHENTICATED_ANONYMOUSLY")
                .anyRequest().access("IS_AUTHENTICATED_FULLY");

        /**-------------  绑定自定义异常处理器：401，403  ------------**/
        http.exceptionHandling()
                .accessDeniedHandler(new ResourceAccessDeniedHandler())
                .authenticationEntryPoint(new Http401AuthenticationEntryPoint());

        /** ------------  自定义SecurityContext持久化策略，修改Session中存储数据类型为UserDto ---------- **/
        http.securityContext().securityContextRepository(new HttpSessionSecurityContextRepository());

        /** ------------  关闭cache，避免session分布式存储到redis中时导致反序列化问题  ---------- **/
        http.requestCache().requestCache(new NullRequestCache());

        /**-------------  其他安全配置  ------------**/
        http.headers()
                .xssProtection().xssProtectionEnabled(false).and()  // 开启XSS攻击防护
                .frameOptions().sameOrigin();  // 允许页面加载同源下的iframe
        http.sessionManagement()
                .maximumSessions(10)
                .sessionRegistry(getSessionRegistry())
                .expiredUrl("/");
//        http.csrf().csrfTokenRepository(new HttpSessionCsrfTokenRepository());  // 开启CSRF防护
        http.csrf().disable();
        http.cors(); // 开启Cors跨域,会自动加载CorsFilter
        http.rememberMe();
        http.addFilterAt(getLogoutFilter(), LogoutFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(defaultUserDetailsService()).passwordEncoder(new BCryptPasswordEncoder())
                .and().eraseCredentials(false);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(ignoreUrl.split(","));
        web.securityInterceptor(resourceFilterSecurityInterceptor());
        super.configure(web);
    }
}

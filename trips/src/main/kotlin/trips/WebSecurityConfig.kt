package trips

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurityConfig: WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {

        http
                .exceptionHandling().authenticationEntryPoint {req,response,e ->
                    response.setHeader("WWW-Authenticate","cookie")
                    response.sendError(401)
                }.and()
                .authorizeRequests()
                .antMatchers("/swagger*/**", "/v3/api-docs", "/actuator/**").permitAll()
                .antMatchers("/api/trips/").permitAll()
                .antMatchers("/api/trips/collection_v0_001").permitAll()
                .antMatchers("/api/trips/collection_v0_002").permitAll()
                .antMatchers("/api/trips/collection_v0_003").permitAll()
                .antMatchers("/api/trips/collection_v1_000").permitAll()
                .antMatchers(HttpMethod.PUT,"/api/trips/{tripId}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PATCH,"/api/trips/{tripId}").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET,"/api/trips/{tripId}").permitAll()
                .and()
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)

    }
}




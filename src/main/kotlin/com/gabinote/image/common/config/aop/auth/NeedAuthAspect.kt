package com.gabinote.image.common.config.aop.auth

import com.gabinote.image.common.util.context.UserContext
import com.gabinote.image.common.util.exception.controller.GatewayAuthFailed
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(2)
@Aspect
@Component
class NeedAuthAspect(
    private val userContext: UserContext
) {
    @Before("@annotation(needAuth)")
    fun checkUserLoggedIn(joinPoint: JoinPoint, needAuth: NeedAuth) {
        if (!userContext.isAuthorized) {
            throw GatewayAuthFailed()
        }
    }
}
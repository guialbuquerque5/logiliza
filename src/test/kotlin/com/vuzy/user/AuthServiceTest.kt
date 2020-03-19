package com.vuzy.user

import com.vuzy.user.models.User
import com.vuzy.user.services.auth.AuthService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jwt.JWK
import io.vertx.ext.jwt.JWT
import io.vertx.kotlin.core.json.array
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import kotlinx.coroutines.runBlocking


class AuthServiceTest {
    private lateinit var authService: AuthService
    private val vertx = Vertx.vertx()

    val config = json {
        obj(
            "jwt" to obj(
                "secret" to "123456"
            ),
            "mysql" to obj(
                "port" to 3306,
                "host" to "mobi7-homolog.cbnh4ax8nd1z.us-east-1.rds.amazonaws.com",
                "database" to "mobi7_erp",
                "user" to "bassan",
                "password" to "m0b17h0m0l0g"
            ),
            "cognito" to array(
                json{ obj (
                    "alg" to "RS256",
                    "e" to "AQAB",
                    "kid" to "WdVU2vBBtXjoS7hE2pZ0iqT8Kz7KawMQEQUgUs8z48g=",
                    "kty" to "RSA",
                    "n" to "gVE18JlgkFSUPHvjg1WOVHYMMSgP_ba369Yj4I6V8EFLL-thPqBAGngs9w4ycWF_Z2spiGC32JmgKJ5kHmc4OtgsCFRSCg7pU1A0fTOg6y8-Rg_IgVzEW4X4U0fYqxCFtvhwX3tJEZAvuKGpKTUVhWx_bfCpCorUyc53XRHnCWf1VgB0GqLOLPzdM9JG3NqHLP0PE3YtF6PdTW3wIL_NKEhmTmdBYhimWaZZzu7zMjUtIE_yBQ4-ne_Bank3lhWZzvq8BdP7vopIgihI643f0Er-Ohsue9nhH32I-16Q5BBs0L5O9iZAAaj2f4hwwgoCcGgqfTlIHsO_TN3wVlDTBw",
                    "use" to "sig"
                )},
                json { obj(
                    "alg" to "RS256",
                    "e" to "AQAB",
                    "kid" to "8VLyKQ7aTal+YDcH2aTLDSOwF1qo4oNQWcYROdpGJXI=",
                    "kty" to "RSA",
                    "n" to "hW9LoqMpFs34nBYA22TuWQZEhWYhPDsJ5hy7UUKAd3gOOtuAaxCOEAUXT1yzWHUWTDqBXYxp8qZRBhiW31aYJksH-e8tVQHNh_31mhsARzbb5PVBjsebfnIn4ylz17Xf8Mt2BZsQ7tc8K889WpVlINqoJLxGdWeAC_vpNytMcs3rvEi2XvZZehinU8TnABo2dc8JW9BKr7XvLWatk_2FZq79n9_fLIg8MdPhgaT9WFLeFBttMOodFib_rcNF7SiUHSi_kUdzPZ3t0O36xs-TC1_QDchSq-O-XbH2DKCsU2yMg6wbtPWVbIxZsEscc4Qv4iHGwJUdRLt9_vSRT4EEXQ",
                    "use" to "sig"
                ) }
            )
        )
    }

    @Test
    fun authorizationServiceTest() = runBlocking {

        val jwt = JWT()
        config.getJsonArray("cognito").map { it as JsonObject }
            .forEach { jwt?.addJWK(JWK(it)) }

        authService = AuthService(jwt)

        val token = "eyJraWQiOiJXZFZVMnZCQnRYam9TN2hFMnBaMGlxVDhLejdLYXdNUUVRVWdVczh6NDhnPSIsImFsZyI6IlJTMjU2In0.eyJhdF9oYXNoIjoid2Z2cnBnX3ZVM1BCdXFlbmRGUmRXUSIsInN1YiI6ImZhODZmNjdkLTBkYTAtNDZlYy04YTcyLWE1MDg4YzRjMjdhMSIsImNvZ25pdG86Z3JvdXBzIjpbInVzLWVhc3QtMV9aQU44MlRvWlNfT2ZmaWNlMzY1Il0sImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwiaXNzIjoiaHR0cHM6XC9cL2NvZ25pdG8taWRwLnVzLWVhc3QtMS5hbWF6b25hd3MuY29tXC91cy1lYXN0LTFfWkFOODJUb1pTIiwiY29nbml0bzp1c2VybmFtZSI6Ik9mZmljZTM2NV9ndWlsaGVybWUuYWxidXF1ZXJxdWVAbW9iaTcuY29tLmJyIiwibm9uY2UiOiJqWWd5Yy1jZmpmUjV6NVJoU2VQVjJOa3RyQWtBb2JYYXlNekluZ3hjTE1la0x2ZVBOaHl3TjhLSnp1UXZKNEtpdXVjUmpxTl92QWdhRy12a0ZMQ21OR2tUVTNwOU5kV24zeTI3WkFrWjRWWElJMHhMRkM4aVNkMVpSTE5XNzF1bXd4NVNmVERiVG5fNTRnUF9UTEpwdGtEb2cydklUaUZYUW40VkZtSlU0YlEiLCJhdWQiOiI0dXFjdGMxNzZjc2hsdm9kbzVoNm9nbGdlNiIsImlkZW50aXRpZXMiOlt7InVzZXJJZCI6Imd1aWxoZXJtZS5hbGJ1cXVlcnF1ZUBtb2JpNy5jb20uYnIiLCJwcm92aWRlck5hbWUiOiJPZmZpY2UzNjUiLCJwcm92aWRlclR5cGUiOiJTQU1MIiwiaXNzdWVyIjoiaHR0cHM6XC9cL3N0cy53aW5kb3dzLm5ldFwvODAwNGI1MWMtNjFlNC00ZTg5LTk2ZjAtODBhOGNhMzZkZWY5XC8iLCJwcmltYXJ5IjoidHJ1ZSIsImRhdGVDcmVhdGVkIjoiMTU4MDMzMTk0Njg0MCJ9XSwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE1ODA4MjM1MTUsIm5hbWUiOiJBbGJ1cXVlcnF1ZSIsImV4cCI6MTU4MDgyNzExNSwiaWF0IjoxNTgwODIzNTE2LCJlbWFpbCI6Imd1aWxoZXJtZS5hbGJ1cXVlcnF1ZUBtb2JpNy5jb20uYnIifQ.TiWoa-7nhwnUpLqiPEcnqrVlv-X0e9wYpfep5qQ7S97_eLBO_fh_ocpnLdE0H_f7bKi-kMIQt_ZcnDXco_Hc6zwT-Rn4Cdot1Ywru36b4AAbz6ckphSMSYOjBu4kC1E_gKF53b_-UBAXH1yOYqiCLCMTeAvBXOfnz5Gj-xO0mkQpAaphzOGMdKP2kYBaazk2eTRPYUeRyVQOtZnc9KCi3QynFjP2SCa_DF0NvNKxJTXMgWoXQ-IjOtH1QywvfgmbCVqoPx5MNao3xA4cenjxqZhHenIHbzoKTbKPpxdCaQ_yM4wzBrtzqsaxmwP33Z9FPdU870YiYx6NzJsxkOeqzw"

        val decoded = authService.tokenToUser(token, checkExpired = false)

        val err = authService.tokenToUser(token, checkExpired = true)

        assertEquals(decoded, User("guilherme.albuquerque@mobi7.com.br"))
        assertNull(err)
    }

}

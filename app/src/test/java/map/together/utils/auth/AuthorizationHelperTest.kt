package map.together.utils.auth

import map.together.model.UserSignUpInfo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AuthorizationHelperTest {
    @Test
    internal fun checkCorrectnessInputData() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "123456",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.CORRECT,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectEmail() {
        val user = UserSignUpInfo(
            "test@test",
            "123456",
            "123456",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_EMAIL,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectPassword_Small() {
        val user = UserSignUpInfo(
            "test@test.test",
            "1234",
            "1234",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_PASSWORD,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectPassword_Empty() {
        val user = UserSignUpInfo(
            "test@test.test",
            "",
            "1234",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_PASSWORD,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectPassword_Symbols() {
        val user = UserSignUpInfo(
            "test@test.test",
            "12+34-",
            "1234",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_PASSWORD,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectPassword_Big() {
        val user = UserSignUpInfo(
            "test@test.test",
            "12345678910121314151617118192021232224252627282293231393435363738",
            "1234",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_PASSWORD,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectConfirmation() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "1234",
            "User",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_CONFIRM_PASSWORD,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectName_Empty() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "123456",
            "",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_NAME,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectName_Small() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "123456",
            "1",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_NAME,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectName_Big() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "123456",
            "agjwafgwfwygfiwegfisggdsigsiegyoewygowegsnrtyedkghdkdgrfrdgretetretwwt",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_NAME,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun checkCorrectnessInputData_incorrectName_Symbols() {
        val user = UserSignUpInfo(
            "test@test.test",
            "123456",
            "123456",
            "1234,.!@#$%^*&)&*(&^*&%$^#",
            ""
        )
        Assertions.assertEquals(
            SignUpDataCorrectType.INCORRECT_NAME,
            AuthorizationHelper.checkCorrectnessInputData(user)
        )
    }

    @Test
    internal fun isCorrectName() {
        Assertions.assertEquals(
            true,
            AuthorizationHelper.isCorrectName("user")
        )
    }

    @Test
    internal fun isCorrectName_RU() {
        Assertions.assertEquals(
            true,
            AuthorizationHelper.isCorrectName("юзер")
        )
    }

    @Test
    internal fun isCorrectName_Empty() {
        Assertions.assertEquals(
            false,
            AuthorizationHelper.isCorrectName("")
        )
    }

    @Test
    internal fun isCorrectName_Big() {
        Assertions.assertEquals(
            false,
            AuthorizationHelper.isCorrectName("khsuhkhuhhhuhiuhiuhkhhkhkhkljljljllnlhkhkhkhkhkhkhkhkhk")
        )
    }

    @Test
    internal fun isCorrectName_Small() {
        Assertions.assertEquals(
            false,
            AuthorizationHelper.isCorrectName("k")
        )
    }

    @Test
    internal fun isCorrectName_Symbols() {
        Assertions.assertEquals(
            false,
            AuthorizationHelper.isCorrectName("k-")
        )
    }
}
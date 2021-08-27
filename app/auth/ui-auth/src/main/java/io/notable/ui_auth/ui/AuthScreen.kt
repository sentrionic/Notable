package io.notable.ui_auth.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.notable.shared.components.DefaultScreenUI
import io.notable.ui_auth.R
import io.notable.ui_auth.ui.components.AuthButton
import io.notable.ui_auth.ui.components.AuthTextField
import io.notable.ui_auth.ui.components.PasswordField

@ExperimentalComposeUiApi
@Composable
fun AuthScreen(
    state: AuthState,
    events: (AuthEvents) -> Unit,
) {
    val (passwordRequest) = FocusRequester.createRefs()
    val controller = LocalSoftwareKeyboardController.current

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = {
            events(AuthEvents.OnRemoveHeadFromQueue)
        },
        progressBarState = state.progressBarState,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Row(
                    modifier = Modifier
                        .height(140.dp)
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.icon),
                        contentDescription = "Icon",
                    )
                }

                Spacer(modifier = Modifier.size(40.dp))

                AuthTextField(
                    placeholder = "Email",
                    state = state.email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { passwordRequest.requestFocus() }
                    ),
                )

                Spacer(modifier = Modifier.size(20.dp))

                PasswordField(
                    state = state.password,
                    passwordRequest = passwordRequest,
                    keyboardActions = KeyboardActions(
                        onDone = { controller?.hide() }
                    )
                )

                Spacer(modifier = Modifier.size(80.dp))

                AuthButton(
                    text = "Register",
                    handleClick = { events(AuthEvents.RegisterPressed) },
                    isEnabled = state.email.isValid && state.password.isValid,
                    color = MaterialTheme.colors.primary
                )

                Spacer(modifier = Modifier.size(30.dp))

                AuthButton(
                    text = "Login",
                    handleClick = { events(AuthEvents.LoginPressed) },
                    isEnabled = state.email.isValid && state.password.isValid,
                    color = Color.Gray
                )
            }
        }
    }
}
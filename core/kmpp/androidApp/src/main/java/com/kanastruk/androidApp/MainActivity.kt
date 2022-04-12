package com.kanastruk.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.kanastruk.mvi.UserProfileViewEvent.EditField
import com.kanastruk.androidApp.ui.state.EmailState
import com.kanastruk.androidApp.ui.theme.CustomTheme
import com.kanastruk.data.core.Address
import com.kanastruk.data.core.Contact
import com.kanastruk.mvi.*
import com.kanastruk.mvi.UserProfileEditorState.*
import com.kanastruk.mvi.UserProfileField.*
import com.kanastruk.mvi.fb.FbAuthState.SignedIn
import com.kanastruk.shared.Greeting
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber

fun greet(): String {
    return Greeting().greeting()
}

fun crashlyticCrashTest() {
    throw RuntimeException("Crashlytics test crash")
}

class MainActivity : ComponentActivity() {
    private val app by lazy { (application as App) }

    // TODO: Add proper DI/SL lib to the project.
    private val firebaseModel by lazy { app.firebaseModel }
    private val editUserProfileModel by lazy { app.editUserProfileModel }
    // ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomTheme {
                ScaffoldBox(
                    "Editor",
                    content = {
                        val fbAuthState = firebaseModel.store.collectAsState()
                        when(fbAuthState.value) {
                            is SignedIn -> Editor(editUserProfileModel.store) { viewEvent ->
                                Timber.d("🤳 viewEvent: $viewEvent")
                                editUserProfileModel.dispatch(viewEvent)
                            }
                            else -> {
                                CircularProgressIndicator()
                                Text(text = "Authenticating")
                            }
                        }
                    }
                )
            }
        }
        lifecycleScope.launch {
            editUserProfileModel.store.collect { state ->
                Timber.d("👩🏻‍💻\uD83D\uDCF2 $state")
            }
        }
        lifecycleScope.launch {
            firebaseModel.store.collect { state ->
                Timber.d("🔥\uD83D\uDCF2 $state")
            }
        }
    }
}

val maxWidthPad20dp = Modifier
    .fillMaxWidth()
    .padding(horizontal = 20.dp)

val fillMaxWidth = Modifier.fillMaxWidth()

@Composable
private fun ScaffoldSandbox(content:@Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sandbox Preview") }
            )
        },
        content = {
            LazyColumn {
                item {
                    Box(
                        modifier = maxWidthPad20dp.background(Color.Magenta),
                        contentAlignment = Alignment.TopStart
                    ) {
                        content()
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun Sandbox() {
    fun wrap(state:UserProfileEditorState):StateFlow<UserProfileEditorState> =
        MutableStateFlow(state).asStateFlow()

    val contact = Contact()
    val address = Address()
    val dirtyCheck: Editing.() -> Boolean = {
        // Compare with "source" contact & address.
        contact != toContact() || address != toAddress()
    }
    val states = listOf(
        Closed,
        Opening,
        Editing(contact, address, dirtyCheck),
        Saving
    )

    CustomTheme {
        ScaffoldBox(title = "Sandbox Editor Test") {
            states.forEach {
                Editor(wrap(it)) { ve ->
                    Timber.d("🤳 viewEvent: $ve")
                }
                Spacer(modifier = Modifier
                    .width(8.dp)
                    .background(Color.Green)
                    .border(2.dp, Color.Magenta, RoundedCornerShape(4.dp)))
            }
        }
    }
}

@Composable
private fun Editor(
    stateFlow: StateFlow<UserProfileEditorState>,
    emitter: (UserProfileViewEvent) -> Unit
) {
    val state = stateFlow.collectAsState()
    when (val value = state.value) {
        Closed -> {
            Button(onClick = { emitter(UserProfileViewEvent.OpenEditor) }) {
                Text(text = "Edit User Profile")// stringResource(id = "Edit User Profile"))
            }
        }
        Opening -> {
            CircularProgressIndicator()
            Text(text = "Loading")
        }
        is Editing -> {
            ContactEditor(value, emitter)
            AddressEditor(value, emitter)
            EditorControls(stateFlow, emitter)
        }
        Saving -> {
            CircularProgressIndicator()
            Text(text = "Saving")
        }
    }
}

@Composable
private fun EditorControls(
    stateFlow: StateFlow<UserProfileEditorState>,
    emitter: (UserProfileViewEvent) -> Unit
) {
    Row(
        modifier = fillMaxWidth,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = { emitter(UserProfileViewEvent.Cancel) },
        ) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { emitter(UserProfileViewEvent.Save) }) {
            Text(text = "Save")
        }
    }
}

@Composable
private fun ScaffoldBox(title:String, content:@Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) }
            )
        },
        content = {
            LazyColumn {
                item {
                    Box(
                        modifier = maxWidthPad20dp,
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = fillMaxWidth.padding(8.dp)
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun OldEditor(stateFlow: StateFlow<UserProfileEditorState>, emitter: (UserProfileViewEvent) -> Unit) {
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(44.dp))
            Box(
                modifier = maxWidthPad20dp, // .background(Color.Magenta),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = fillMaxWidth.padding(8.dp)
                ) {
                    Editor(stateFlow, emitter)
                }
            }
        }
    }
}

@Composable
private fun LoginForm() {
    val focusRequester = remember { FocusRequester() }
    val emailState = remember { EmailState() }

    Text(modifier = fillMaxWidth, text = greet())
    OutlinedTextField(
        value = emailState.text,
        onValueChange = { x ->
            emailState.text = x
        },
        modifier = fillMaxWidth,
        label = { Text("Email") }
    )
    OutlinedTextField(
        value = emailState.text,
        onValueChange = { x ->
            emailState.text = x
        },
        modifier = fillMaxWidth,
        label = { Text("Alt. Email") }
    )
    Row(
        modifier = fillMaxWidth,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        OutlinedButton(
            onClick = { /*TODO*/ },
        ) {
            Text(text = "Anony 🐁")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { }) {
            Text(text = stringResource(id = R.string.email_sign_in))
        }
    }
}

@Composable
private fun ContactEditor(
    value: Editing,
    emitter: (UserProfileViewEvent) -> Unit
) {
    OutlinedTextField(
        label = { Text(stringResource(R.string.firstName)) },
        value = value.fieldMap[FIRST]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(FIRST, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.lastName)) },
        value = value.fieldMap[LAST]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(LAST, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.companyName)) },
        value = value.fieldMap[COMPANY]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(COMPANY, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.email)) },
        value = value.fieldMap[EMAIL]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(EMAIL, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.phone)) },
        value = value.fieldMap[PHONE]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(PHONE, changedText)) },
        modifier = fillMaxWidth,
    )
}

@Composable
private fun AddressEditor(
    value: Editing,
    emitter: (UserProfileViewEvent) -> Unit
) {
    OutlinedTextField(
        label = { Text(stringResource(R.string.address1)) },
        value = value.fieldMap[ADDRESS1]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(ADDRESS1, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.address2)) },
        value = value.fieldMap[ADDRESS2]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(ADDRESS2, changedText)) },
        modifier = fillMaxWidth,
    )
    Row(modifier = fillMaxWidth) {
        OutlinedTextField(
            label = { Text(stringResource(R.string.city)) },
            value = value.fieldMap[CITY]!!.text,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            onValueChange = { changedText -> emitter(EditField(CITY, changedText)) },
            modifier = Modifier.weight(1f, true)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            label = { Text(stringResource(R.string.country_sub)) },
            value = value.fieldMap[COUNTRY_SUB]!!.text,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            onValueChange = { changedText -> emitter(EditField(COUNTRY_SUB, changedText)) },
            modifier = Modifier.weight(1f, true)
        )
    }
    OutlinedTextField(
        label = { Text(stringResource(R.string.postal)) },
        value = value.fieldMap[POSTAL]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(POSTAL, changedText)) },
        modifier = fillMaxWidth,
    )
    OutlinedTextField(
        label = { Text(stringResource(R.string.country)) },
        value = value.fieldMap[COUNTRY]!!.text,
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        onValueChange = { changedText -> emitter(EditField(COUNTRY, changedText)) },
        modifier = fillMaxWidth,
    )
}

@Composable
fun GreetingColumn() {
    LazyColumn {
        item {
            Spacer(modifier = Modifier.height(44.dp))
            LoginBox()
//            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = maxWidthPad20dp) {
                Button(onClick = ::crashlyticCrashTest) {
                    Text(text = stringResource(id = R.string.crash_test))
                }
            }
        }
    }
}

@Composable
private fun LoginBox() {
    Box(
        modifier = maxWidthPad20dp, // .background(Color.Magenta),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = fillMaxWidth // .background(Color.Cyan)
        ) {
            LoginForm()
        }
    }
}

@Composable
fun CustomTopAppBar() {
    TopAppBar(
        title = { Text(text = "🔥base Auth") }
    )
}

@Composable
fun GreetingComposable() {
    CustomTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Hello?") }
                )
            },
            content = {
                LazyColumn {
                    item {
                        Spacer(modifier = Modifier.height(44.dp))
                        Box() {
                            Column() {
                                Text(greet())
                                TextButton(
                                    onClick = ::crashlyticCrashTest
                                ) {
                                    Text(text = stringResource(id = R.string.crash_test))
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

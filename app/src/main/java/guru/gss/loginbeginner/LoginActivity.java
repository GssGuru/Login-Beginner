package guru.gss.loginbeginner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    /*
    ENG: prepare TAG elements
    RU: подготовить элементы TAG
    */
    private final String TAG = "gss.guru";

    /*
    ENG: prepare elements for internet request
    RU: подготовить элементы для интернет-запроса
    */
    private final MediaType REQWEST_HEADERS = MediaType.get("application/json; charset=utf-8");
    private final String URL = "https://gss.guru/api/authorization";
    private OkHttpClient client = new OkHttpClient();
    private UserLoginTask mAuthTask = null;

    /*
    ENG: prepare Views elements
    RU: подготовить элементы Views
    */
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*
        ENG: initialize the views and click on the button
        RU:инициализировать views и нажатие на кнопку
        */
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logIn();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        sPref = getPreferences(MODE_PRIVATE);
        loadEmailAndPasswd();
    }

    /*
    ENG: get info from views and validate it and if is information is valid - do internet request and get result from server
    RU: получить информацию из views и проверить ее, и если информация действительна - сделать запрос в Интернете и получить результат с сервера
    */
    private void logIn() {
        if (mAuthTask != null) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError("This password is too short");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("This field is required");
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError("This email address is invalid");
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showLoadingDialog(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /*
    ENG: method of animation with views and progress and
    RU: метод анимации с Views и progress и
    */
    private void showLoadingDialog(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /*
    ENG: Send POST Internet request with parameters(Email and password) and get result
    RU: Отправьте POST-запрос в Интернет с параметрами (адрес электронной почты и пароль) и получите результат
    */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            String content = mEmail + mPassword;
            RequestBody body = RequestBody.create(REQWEST_HEADERS, content);
            Request request = new Request.Builder()
                    .url(URL)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    try {
                        return response.body().string();
                    } catch (IOException e) {
                        Log.e(TAG, "UserLoginTask.doInBackground", e);
                    }
                } else {
                    Log.e(TAG, "UserLoginTask.doInBackground response.body() != null");
                }
                return null;
            } catch (IOException e) {
                Log.e(TAG, "UserLoginTask.doInBackground", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String requestResult) {
            closeTask();
            if (requestResult != null) {
                try {
                    JSONObject argJSON = new JSONObject(requestResult);
                    String response = argJSON.getString("response");
                    JSONObject responseJSON = new JSONObject(response);
                    String status = responseJSON.getString("status");
                    if (status.equals("saccess")) {
                        saveEmailAndPasswd(mEmail, mPassword);
                        Toast.makeText(LoginActivity.this, "Авторизация успешна", Toast.LENGTH_SHORT).show();
                        /*
                         * TODO
                         * Поздровляю))) Ми залогинелись
                         */
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "UserLoginTask.onPostExecute", e);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            closeTask();
        }

        private void closeTask() {
            mAuthTask = null;
            showLoadingDialog(false);
        }
    }

    /*
    ENG: Save email and password in storage(Shared Preference) and using it in next pages in the application
    RU: Сохраните электронную почту и пароль в хранилище (Shared Preference) и используйте его на следующих страницах приложения
    */
    private SharedPreferences sPref;
    private final String SAVED_TEXT_EMAIL = "saved_text_email";
    private final String SAVED_TEXT_PASSWORD = "saved_text_password";

    private void saveEmailAndPasswd(String email, String password) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT_EMAIL, email);
        ed.putString(SAVED_TEXT_PASSWORD, password);
        ed.commit();
    }

    private void loadEmailAndPasswd() {
        String email = sPref.getString(SAVED_TEXT_EMAIL, "");
        String password = sPref.getString(SAVED_TEXT_PASSWORD, "");
        mEmailView.setText(email);
        mPasswordView.setText(password);
    }
}

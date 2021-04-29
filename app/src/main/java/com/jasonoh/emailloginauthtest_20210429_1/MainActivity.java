package com.jasonoh.emailloginauthtest_20210429_1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button btnSendMail, btnSendMailCK;
    EditText etEmail, etEmailCK;
    TextView tvTimeOut;

    String randomNum = "";

    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 180 * 1000; //총 시간 (180초 = 3분)
    final int COUNT_DOWN_INTERVAL = 1000; //onTick 메소드를 호출할 간격 (1초)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .permitDiskReads()
        .permitDiskWrites()
        .permitNetwork().build());

        btnSendMailCK = findViewById(R.id.btn_send_mail_ck);
        etEmailCK = findViewById(R.id.et_email_ck);
        btnSendMail = findViewById(R.id.btn_send_mail);
        btnSendMailCK.setEnabled(false);
        etEmailCK.setEnabled(false);
        etEmail = findViewById(R.id.et_email);
        tvTimeOut = findViewById(R.id.tv_time_out);
        btnSendMail.setOnClickListener(v -> {

            if(etEmail.getText().toString().equals("") || !Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()){
                new AlertDialog.Builder(this).setMessage("이메일 형식에 맞게 입력해주시기 바랍니다.").setNegativeButton("확인", null).create().show();
                return;
            }

            randomNum = numberGen(6, 2);

            SendMail mailServer = new SendMail();
            mailServer.sendSecurityCode(getApplicationContext(), etEmail.getText().toString(), randomNum);

//            초시계 형성
            countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
                @Override
                public void onTick(long millisUntilFinished) { //(300초에서 1초 마다 계속 줄어듬)

                    long emailAuthCount = millisUntilFinished / 1000;
                    Log.d("Alex", emailAuthCount + "");

                    if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                        tvTimeOut.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                    } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                        tvTimeOut.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                    }

                    //emailAuthCount은 종료까지 남은 시간임. 1분 = 60초 되므로,
                    // 분을 나타내기 위해서는 종료까지 남은 총 시간에 60을 나눠주면 그 몫이 분이 된다.
                    // 분을 제외하고 남은 초를 나타내기 위해서는, (총 남은 시간 - (분*60) = 남은 초) 로 하면 된다.

                }


                @Override
                public void onFinish() { //시간이 다 되면 인증번호 없애기

                    new AlertDialog.Builder(MainActivity.this).setMessage("인증번호 입력 시간이 지났습니다.\n재인증해주시기 바랍니다.").setNegativeButton("확인", null).create().show();
                    tvTimeOut.setText("03:00");

                    randomNum = "";
                    etEmailCK.setEnabled(false);


                    btnSendMailCK.setEnabled(false);
                }
            }.start();

            btnSendMailCK.setEnabled(true);
            etEmailCK.setEnabled(true);
        });

        btnSendMailCK.setOnClickListener(v->{
            if(!randomNum.equals("") && randomNum.equals(etEmailCK.getText().toString())){
                new AlertDialog.Builder(this).setMessage("인증번호가 일치합니다").setNegativeButton("확인", null).create().show();
                countDownTimer.cancel();
//                원래 이 이후에는 추가적으로 눌러도 인증코드 확인인 안된다.
                btnSendMailCK.setEnabled(false);
                etEmailCK.setEnabled(false);
                tvTimeOut.setText("03:00");
                etEmail.setText("");
            } else{
                new AlertDialog.Builder(this).setMessage("인증번호를 다시 확인해주세요").setNegativeButton("확인", null).create().show();
            }
        });

    }

    /**
     * 전달된 파라미터에 맞게 난수를 생성한다
     * @param len : 생성할 난수의 길이
     * @param dupCd : 중복 허용 여부 (1: 중복허용, 2:중복제거)
     *
     * Created by 닢향
     * http://niphyang.tistory.com
     */
    public static String numberGen(int len, int dupCd ) {

        Random rand = new Random();
        String numStr = ""; //난수가 저장될 변수

        for(int i=0;i<len;i++) {

            //0~9 까지 난수 생성
            String ran = Integer.toString(rand.nextInt(10));

            if(dupCd==1) {
                //중복 허용시 numStr에 append
                numStr += ran;
            }else if(dupCd==2) {
                //중복을 허용하지 않을시 중복된 값이 있는지 검사한다
                if(!numStr.contains(ran)) {
                    //중복된 값이 없으면 numStr에 append
                    numStr += ran;
                }else {
                    //생성된 난수가 중복되면 루틴을 다시 실행한다
                    i-=1;
                }
            }
        }
        return numStr;
    } // numberGen method

}
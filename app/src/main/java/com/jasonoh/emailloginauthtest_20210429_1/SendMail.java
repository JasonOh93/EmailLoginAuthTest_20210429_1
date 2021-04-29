package com.jasonoh.emailloginauthtest_20210429_1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;

public class SendMail extends AppCompatActivity {

    String user;
    String password;

    public void sendSecurityCode(Context context, String sendTo, String randomNum){
        try {
            user = context.getResources().getString(R.string.gmail_id);
            password = context.getResources().getString(R.string.gmail_password);

//            Log.e("TAG", user + "   :::   " + password);

            GmailSender gmailSender = new GmailSender(user, password);

            gmailSender.sendMail("테스트용으로 보내는 인증메일입니다.",  "회원가입을 하기위한 인증번호는 [ "+ randomNum +" ] 입니다.", sendTo);
            Toast.makeText(context, "이메일을 성공적으로 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SendFailedException e){
            Toast.makeText(context, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e){
            Log.e("error", e + "");
            e.printStackTrace();
//            https://myaccount.google.com/lesssecureapps?pli=1&rapt=AEjHL4ODv4BRy7rgya9iwYt8of-PdlzRq7KJlVZdz4_HGjNWJjVormQK9d3ESaTKKpsJfmcnHdxE9lMgdWtFAu_XtvAKJvg5WA
//            구글 보안 수준이 낮은 앱의 액세스를 사용으로 허용하는 것
//            https://mail.google.com/mail/u/0/#settings/fwdandpop
//            구글에서 POP/IMAP를 사용으로 설정해야 하는 것
            Toast.makeText(context, "인터넷 연결을 확인해주십시오.", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


}
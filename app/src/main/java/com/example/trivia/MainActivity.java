package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAyncResposnse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.util.Prefs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String MESSAGE_ID = "message";
    private TextView question;
    private TextView scoreView;
    private int score = 0;
    private int highScore = 0;
    private TextView counterQuestion;
    private Button trueButton;
    private Button falseButton;
    private ImageButton next;
    private ImageButton previous;
    private int currentQuestion = 0;
    private List<Question> questions;

    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.question);
        scoreView = findViewById(R.id.scoreView);
        counterQuestion = findViewById(R.id.counter);
        trueButton = findViewById(R.id.trueButton);
        falseButton = findViewById(R.id.falseButton);
        next = findViewById(R.id.nextButton);
        previous = findViewById(R.id.previousButton);
        prefs = new Prefs(MainActivity.this);

        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        highScore = prefs.getHighScore();
        currentQuestion = prefs.getState();

        questions = new QuestionBank().getQuestions(new AnswerListAyncResposnse() {
            @Override
            public void processFinished(ArrayList<Question> questions) {
                question.setText(questions.get(currentQuestion).getAnswer());
                counterQuestion.setText(MessageFormat.format("{0}/{1}", currentQuestion, questions.size()));
                scoreView.setText(MessageFormat.format("Current score: {0}\nHighest score: {1}", score, highScore));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextButton:
                goNext();
                break;
            case R.id.previousButton:
                if (currentQuestion > 0){
                    currentQuestion = (currentQuestion - 1) % questions.size();
                    updateQuestion();
                }
                break;
            case R.id.trueButton:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.falseButton:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void checkAnswer(boolean userChoice) {

        boolean trueAnswer = questions.get(currentQuestion).isAnswerTrue();
        int toastMessageId = 0;
        if (userChoice == trueAnswer){
            score += 10;
            fadeView();
            toastMessageId = R.string.correctAnswer;
        }
        else{
            if (score > 0) score -= 10;
            shakeAnimation();
            toastMessageId = R.string.wrongAnswer;
        }
        if (score > highScore) {
            highScore = score;
        }
        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
        scoreView.setText(MessageFormat.format("Current score: {0}\nHighest score: {1}", score, prefs.getHighScore()));
    }

    private void updateQuestion() {
        String q = questions.get(currentQuestion).getAnswer();
        question.setText(q);
        counterQuestion.setText(MessageFormat.format("{0}/{1}", currentQuestion, questions.size()));
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);

        cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        prefs.saveHighScore(highScore);
        prefs.setState(currentQuestion);
        super.onPause();
    }

    private void goNext(){
        currentQuestion = (currentQuestion + 1) % questions.size();
        updateQuestion();
    }

    @Override
    protected void onStop() {
        currentQuestion = 0;
        highScore = 0;
        super.onStop();
    }
}

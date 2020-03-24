package com.example.trivia.data;

import com.example.trivia.model.Question;

import java.util.ArrayList;

public interface AnswerListAyncResposnse {
    void processFinished(ArrayList<Question> questions);
}

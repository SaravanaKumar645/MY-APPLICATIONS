package com.example.scientificcalculator;

import java.util.ArrayList;
import java.util.List;

public class Modelclass {
    String expression,result;
    ArrayList<String> listExpression;
    List<String> listResult;
    public Modelclass(){
        //getListExpression();
        //getListResult();
    }

    public Modelclass(String expcopy, String result1){
        this.expression=expcopy;
        this.result=result1;
        getListResult();
        getListExpression();

    }


    public List<String> getListExpression() {

        listExpression.add(expression);
        return listExpression;
    }

    public List<String> getListResult() {
        listResult.add(result);
        return listResult;
    }



}

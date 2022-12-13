package com.ihm.firstapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.Context;
import android.graphics.Point;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Sudoku extends AppCompatActivity {

    private GridLayout layout ;
    private TextView partie;
    private int[][] mat = new int[4][4];
    private int quarterScreenWidth = 0;
    private ArrayList<Integer> complet = new ArrayList<Integer>(), editListIndex = new ArrayList<Integer>();
    private int prt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);

        layout = findViewById(R.id.grid_layout);
        partie = findViewById(R.id.textView9);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        quarterScreenWidth = (int)(size.x*0.25);
        init_edit();

    }

    private void init_mat(){
        for(int i=0; i < 4; i++){
            for(int j=0; j< 4; j++){
                mat[i][j] = 0;
            }
        }
    }

    private boolean possible(int x, int y, int val){
        for(int i=0; i<4;i++){
            if(mat[i][y] == val) return false;
            if(mat[x][i] == val) return false;
        }
        int x1 = (int) ((x/2)*2);
        int y1 = (int) ((y/2)*2);
        for(int i =0; i<2;i++)
            for(int j=0; j<2;j++){
                if(mat[x1+i][y1+j]==val){
                    return false;
                }
            }
        return true;
    }

    private void remplir(int v,int n){
        int x = n%4, y = (int) n/4;
        if(n < 16){
            if(possible(x,y,v)){
                mat[x][y] = v;
                remplir((int) (v%4)+1, n+1);
            }else{
                remplir((int) (v%4)+1, n);
            }
        }

    }
    private void init_edit(){
        ArrayList<Integer> indxs = new ArrayList<Integer>();
        Collections.addAll(indxs, 1,3,4,6,7,8,10,13,15);
        for(int i = 0; i < layout.getChildCount(); i++){
            if(!indxs.contains(i)){
                EditText txtV = findViewById(layout.getChildAt(i).getId());
                editListIndex.add(i);
                txtV.setFilters(new InputFilter[]{ new MinMaxFilter("1", "4")});
                txtV.setWidth(quarterScreenWidth);
                txtV.setHeight(quarterScreenWidth);
                txtV.setOnKeyListener(this::validate);
            }
        }

    }
    private boolean valide_sol(){
        for(int i=0; i<7;i++){
           EditText edt = (EditText) layout.getChildAt(editListIndex.get(i));
           if(Integer.parseInt(edt.getText().toString()) != mat[editListIndex.get(i)%4][editListIndex.get(i)/4]){
               return false;
           }
        }
        return true;
    }
    private void start_round(){
        init_mat();
        remplir((int) (Math.random()*10%4)+1,0);

        ArrayList<Integer> indxs = new ArrayList<Integer>();
        Collections.addAll(indxs, 1,3,4,6,7,8,10,13,15);

        partie.setText(String.format("Partie : %d", prt));
        for(int i = 0; i < layout.getChildCount(); i++){
            if(indxs.contains(i)){
                TextView txtV = findViewById(layout.getChildAt(i).getId());
                txtV.setWidth(quarterScreenWidth);
                txtV.setHeight(quarterScreenWidth);

                txtV.setText(""+mat[i%4][(int) i/4]);
            }else{
                EditText txtV = findViewById(layout.getChildAt(i).getId());
                txtV.setText("");
            }
        }

    }

    private boolean validate(View v, int k, KeyEvent e){

        if(e.getAction() == KeyEvent.ACTION_UP){
            EditText edt = ((EditText) v);
            if(!complet.contains(edt.getId()) && edt.getText().length() == 1 && k >= 8 && k < 12){
                complet.add(edt.getId());
                if(complet.size() == 7 && valide_sol()){
                    prt++;
                    start_round();
                    complet.removeAll(complet.subList(0,7));
                }
            }
            if(complet.contains(edt.getId()) && edt.getText().length() == 0 && e.getKeyCode() == KeyEvent.KEYCODE_DEL){
                complet.remove((Object) edt.getId());
            }
        }
        return false;
    }

    public void demarrer(View v){
        if(layout.getVisibility() == View.GONE){
            v.setVisibility(View.GONE);
            start_round();

            partie.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
            Button annuler = findViewById(R.id.button7);
            annuler.setVisibility(View.VISIBLE);
        }
    }

    public void Annuler(View v){
        prt = 0;
        v.setVisibility(View.GONE);
        layout.setVisibility(View.GONE);
        partie.setVisibility(View.INVISIBLE);
        Button dema = findViewById(R.id.button6);
        dema.setVisibility(View.VISIBLE);
    }


}
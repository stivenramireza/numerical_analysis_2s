package com.example.sacrew.numericov4.fragments.interpolationFragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.sacrew.numericov4.R;
import com.example.sacrew.numericov4.fragments.MainActivityTable;
import com.example.sacrew.numericov4.fragments.customPopUps.popUpBisection;
import com.example.sacrew.numericov4.fragments.customPopUps.popUpNewtonDifferences;


import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.ExpandAll;
import org.matheclipse.parser.client.eval.DoubleEvaluator;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class newtonInterpolator extends baseInterpolationMethods {

    private List<double []> derivs;
    private boolean errorDivision = true;

    public newtonInterpolator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newton_interpolator, container, false);
        Button runHelp = view.findViewById(R.id.runHelp);
        Button run = view.findViewById(R.id.run);
        run.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                calc = false;
                cleanGraph();
                execute();

            }
        });
        Button showEquations = view.findViewById(R.id.showEquations);
        showEquations.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(calc) {
                    Intent i = new Intent(getContext(), mathExpressions.class);
                    Bundle b = new Bundle();

                    b.putString("key","$${"+function+"\\qquad \\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad}$$"); //Your id
                    i.putExtras(b); //Put your id to your next Intent
                    startActivity(i);
                }
            }
        });
        runHelp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                executeHelp();
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void executeHelp() {
        Intent i = new Intent(getContext().getApplicationContext(), popUpNewtonDifferences.class);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void execute() {
        boolean errorValues = bootStrap();
        if(errorValues) {
            derivs = new LinkedList<>();
            newtonInterpolation(fxn, 0, 1, new double[fxn.length - 1]);
            if (errorDivision) {
                StringBuilder uglyFunction = new StringBuilder(String.valueOf(fxn[0]));
                StringBuilder prev = new StringBuilder("");
                for (int i = 1; i < derivs.size(); i++) {
                    prev.append("(x-").append(String.valueOf(xn[i - 1])).append(")");
                    uglyFunction.append("+").append(String.valueOf(derivs.get(i)[0])).append(prev);
                }
                EvalEngine util = new EvalEngine();
                IExpr result = util.evaluate(uglyFunction.toString());


                //to latex
                EvalEngine engine = new EvalEngine(false);
                IExpr outLatex = engine.evaluate(F.Simplify(result));

                TeXUtilities texUtil = new TeXUtilities(engine, false);
                StringWriter stw = new StringWriter();
                texUtil.toTeX(outLatex, stw);
                //add new expression type latex
                function = stw.toString();

                //update graph
                updateGraph(outLatex.toString(), getContext(), (int) Math.ceil(((xn[xn.length - 1] - xn[0]) * 10) + 20));
                //variable to open equations
                calc = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void newtonInterpolation(double[] actualValues, int difference, int i, double[] auxFxn) {
        System.out.println("actual values "+actualValues.length+" i: "+i);
        if (actualValues.length == 1) {
            derivs.add(actualValues);
        } else if (i == actualValues.length) {
            System.out.println("STOP auxFxn: "+auxFxn.length);
            derivs.add(actualValues);
            System.out.println("new length " + (auxFxn.length-1));
            newtonInterpolation(auxFxn, difference + 1, 1, new double[auxFxn.length-1]);

        } else {
            double denominator = (xn[i + difference] - xn[i - 1]);
            if (denominator == 0) {
                Toast.makeText(getContext(), "Error division by 0", Toast.LENGTH_SHORT).show();
                errorDivision = false;
            }
            double newFxn = (actualValues[i] - actualValues[i - 1]) / denominator;
            auxFxn[i-1] = newFxn;
            newtonInterpolation(actualValues, difference, i + 1, auxFxn);
        }
    }
}

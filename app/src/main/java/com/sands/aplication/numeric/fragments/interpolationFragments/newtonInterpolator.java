package com.sands.aplication.numeric.fragments.interpolationFragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sands.aplication.numeric.R;
import com.sands.aplication.numeric.fragments.customPopUps.popUpNewtonDifferences;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.eval.TeXUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class newtonInterpolator extends baseInterpolationMethods {

    private List<double[]> derivs;
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
                if (calc) {
                    Intent i = new Intent(getContext(), mathExpressions.class);
                    Bundle b = new Bundle();

                    b.putString("key", "$${" + latexText + "\\qquad \\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad\\qquad}$$"); //Your id
                    b.putString("function", function);
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
    private void executeHelp() {
        Intent i = new Intent(getContext().getApplicationContext(), popUpNewtonDifferences.class);
        startActivity(i);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void execute() {
        boolean errorValues = bootStrap();
        if (errorValues) {
            derivs = new LinkedList<>();
            newtonInterpolation(fxn, 0, 1, new double[fxn.length - 1]);
            if (errorDivision) {

                StringBuilder uglyFunction = new StringBuilder(String.valueOf(fxn[0]));
                StringBuilder auxToLAtexFunc = new StringBuilder(String.valueOf(roundOff(fxn[0])));
                StringBuilder prev = new StringBuilder("");
                StringBuilder auxPrev = new StringBuilder("");
                for (int i = 1; i < derivs.size(); i++) {
                    prev.append("(x-(").append(String.valueOf(xn[i - 1])).append("))");
                    auxPrev.append("(x-(").append(String.valueOf(roundOff(xn[i - 1]))).append("))");
                    uglyFunction.append("+").append(String.valueOf(derivs.get(i)[0])).append(prev);
                    auxToLAtexFunc.append("+").append(roundOff(derivs.get(i)[0])).append(auxPrev);
                }
                ExprEvaluator util = new ExprEvaluator();

                //to latex
                EvalEngine engine = new EvalEngine(false);

                IExpr simplifiedPFunction = util.evaluate(F.ExpandAll(util.evaluate(uglyFunction.toString())));
                if (Build.VERSION.SDK_INT > 19) {
                    simplifiedPFunction = util.evaluate(F.FullSimplify(simplifiedPFunction));
                }
                function = simplifiedPFunction.toString();
                updateGraph(function, getContext(), (int) Math.ceil(((xn[xn.length - 1] - xn[0]) * 10) + 20));
                TeXUtilities texUtil = new TeXUtilities(engine, false);
                StringWriter stw = new StringWriter();
                texUtil.toTeX(simplifiedPFunction, stw);
                latexText = stw.toString();
                //variable to open equations
                calc = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void newtonInterpolation(double[] actualValues, int difference, int i, double[] auxFxn) {
        if (actualValues.length == 1) {
            derivs.add(actualValues);
        } else if (i == actualValues.length) {
            derivs.add(actualValues);
            newtonInterpolation(auxFxn, difference + 1, 1, new double[auxFxn.length - 1]);

        } else {
            double denominator = (xn[i + difference] - xn[i - 1]);
            if (denominator == 0) {
                styleWrongMessage();
                errorDivision = false;
            }
            double newFxn = (actualValues[i] - actualValues[i - 1]) / denominator;
            auxFxn[i - 1] = newFxn;
            newtonInterpolation(actualValues, difference, i + 1, auxFxn);
        }
    }


}
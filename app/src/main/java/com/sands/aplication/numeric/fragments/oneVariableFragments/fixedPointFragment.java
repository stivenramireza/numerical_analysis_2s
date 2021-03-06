package com.sands.aplication.numeric.fragments.oneVariableFragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.sands.aplication.numeric.R;
import com.sands.aplication.numeric.fragments.customPopUps.popUpFixedPoint;
import com.sands.aplication.numeric.fragments.listViewCustomAdapter.FixedPoint;
import com.sands.aplication.numeric.fragments.listViewCustomAdapter.FixedPointListAdapter;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static com.sands.aplication.numeric.fragments.homeFragment.poolColors;


/**
 * A simple {@link Fragment} subclass.
 */
public class fixedPointFragment extends baseOneVariableFragments {

    private Expression functionG;
    private View view;
    private ListView listView;
    private EditText xvalue;
    private EditText textFunctionG;

    public fixedPointFragment() {

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.fragment_fixed_point, container, false);
        } catch (InflateException e) {
            // ignorable
        }
        Button runFixed = view.findViewById(R.id.runFixed);
        runFixed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                cleanGraph();
                bootStrap();
            }
        });
        Button runHelp = view.findViewById(R.id.runHelp);
        Button runChart = view.findViewById(R.id.runChart);
        runChart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                executeChart(getContext());
            }
        });
        listView = view.findViewById(R.id.listView);
        runHelp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                executeHelp();
            }
        });

        xvalue = view.findViewById(R.id.xValue);
        textFunctionG = view.findViewById(R.id.functionG);


        registerEditText(textFunctionG, getContext(), getActivity());
        registerEditText(xvalue, getContext(), getActivity());
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void executeHelp() {
        Intent i = new Intent(Objects.requireNonNull(getContext()).getApplicationContext(), popUpFixedPoint.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void execute(boolean error, double errorValue, int ite) {
        Double xValue = 0.0;

        String originalFuncG = textFunctionG.getText().toString();
        boolean errorFuncG = checkSyntax(originalFuncG, textFunctionG);
        if (errorFuncG)
            updateMyFunctions(originalFuncG);
        error = errorFuncG && error;
        this.functionG = new Expression(functionRevision(originalFuncG));

        try {
            xValue = Double.parseDouble(xvalue.getText().toString());
        } catch (Exception e) {
            xvalue.setError("Invalid Xi");
            error = false;
        }

        if (error) {
            if (errorToggle.isChecked()) {
                fixedPointMethod(xValue, errorValue, ite, true);
            } else {
                fixedPointMethod(xValue, errorValue, ite, false);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fixedPointMethod(Double x0, Double tol, int ite, boolean errorRel) {
        String message;
        try {
            function.setPrecision(100);
            functionG.setPrecision(100);
            ArrayList<FixedPoint> listValues = new ArrayList<>();
            FixedPoint titles = new FixedPoint("n", "Xn", "f(Xn)", "Error");
            listValues.add(titles);
            listValuesTitles = new LinkedList<>();
            listValuesTitles.add("Xn");
            listValuesTitles.add("f(Xn)");
            listValuesTitles.add("Error");
            completeList = new LinkedList<>();
            if (tol >= 0) {
                if (ite > 0) {
                    double y0 = (this.function.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    if (y0 != 0) {
                        int cont = 0;
                        double error = tol + 1;
                        FixedPoint iteZero = new FixedPoint(String.valueOf(cont), String.valueOf(normalTransformation(x0)), String.valueOf(cientificTransformation(y0)), String.valueOf(cientificTransformation(error)));
                        listValues.add(iteZero);
                        List<String> listValuesIteZero = new LinkedList<>();
                        listValuesIteZero.add(String.valueOf(x0));
                        listValuesIteZero.add(String.valueOf(cientificTransformation(y0)));
                        listValuesIteZero.add(String.valueOf(""));
                        double xa = x0;
                        completeList.add(listValuesIteZero);
                        calc = true;
                        while ((y0 != 0) && (error > tol) && (cont < ite)) {
                            ArrayList<String> listValuesIteNext = new ArrayList<String>();
                            double xn = Double.NaN;
                            try {
                                y0 = Double.NaN;
                                xn = (this.functionG.with("x", BigDecimal.valueOf(xa)).eval()).doubleValue();
                                y0 = (this.function.with("x", BigDecimal.valueOf(xn)).eval()).doubleValue();//correccion xa xnzz
                            } catch (Exception e) {
                                styleWrongMessage("Unexpected error NaN");
                            }

                            if (errorRel)
                                error = Math.abs(xn - xa) / xn;
                            else
                                error = Math.abs(xn - xa);

                            xa = xn;
                            cont++;
                            FixedPoint iteNext = new FixedPoint(String.valueOf(cont), String.valueOf(normalTransformation(xa)), String.valueOf(cientificTransformation(y0)), String.valueOf(cientificTransformation(error)));
                            listValues.add(iteNext);
                            listValuesIteNext.add(String.valueOf(xa));
                            listValuesIteNext.add(String.valueOf(cientificTransformation(y0)));
                            listValuesIteNext.add(String.valueOf(cientificTransformation(error)));
                            completeList.add(listValuesIteNext);

                        }
                        listValues.add(new FixedPoint("", "", "", ""));
                        int color = poolColors.remove(0);
                        poolColors.add(color);

                        graphSerie(function.getExpression(), xa * 2, color);
                        if (y0 == 0) {
                            //graphSerie(xa - 0.2, xa+0.2, function.getExpression(), graph, Color.BLUE);
                            //graphSerie(xa - 0.2, xa+0.2, functionG.getExpression(), graph, Color.RED);
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);
                            //graphPoint(xa, y0, PointsGraphSeries.Shape.POINT, graph, getActivity(), Color.parseColor("#0E9577"), true);
                            //Toast.makeText(getContext(), convertirNormal(xa) + " is a root", Toast.LENGTH_SHORT).show();
                            message = normalTransformation(xa) + " is a root";
                            styleCorrectMessage(message);
                        } else if (error <= tol) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);
                            message = normalTransformation(xa) + " is an aproximate root";
                            styleCorrectMessage(message);
                            //Toast.makeText(getContext(), convertirNormal(xa) + " is an aproximate root", Toast.LENGTH_SHORT).show();
                        } else {
                            message = "Failed the interval!";
                            styleWrongMessage(message);
                            //Toast.makeText(getContext(), "Failed the interval!", Toast.LENGTH_SHORT).show();
                        }
                        graphSerie(function.getExpression(), xa, color);
                        if (y0 == 0) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);
                            styleCorrectMessage(normalTransformation(xa) + " is a root");
                        } else if (error <= tol) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa, y0, color);
                            styleCorrectMessage(normalTransformation(xa) + " is an aproximate root");

                        } else {
                            styleWrongMessage("The method failed with " + ite + " iterations!");
                        }

                    } else {
                        int color = poolColors.remove(0);
                        poolColors.add(color);
                        graphPoint(x0, y0, color);
                        styleCorrectMessage(normalTransformation(x0) + " is an aproximate root");
                    }
                } else {
                    iter.setError("Wrong iterates");
                    styleWrongMessage("Wrong iterates");
                }
            } else {
                textError.setError("Tolerance must be > 0");
                styleWrongMessage("Tolerance must be > 0");
            }
            FixedPointListAdapter adapter = new FixedPointListAdapter(getContext(), R.layout.list_adapter_fixed_point, listValues);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            styleWrongMessage("Unexpected error: " + e.getMessage());
        }
    }


}

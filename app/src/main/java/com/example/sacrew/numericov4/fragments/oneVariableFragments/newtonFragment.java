package com.example.sacrew.numericov4.fragments.oneVariableFragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sacrew.numericov4.R;
import com.example.sacrew.numericov4.fragments.customPopUps.popUpNewton;
import com.example.sacrew.numericov4.fragments.graphFragment;
import com.example.sacrew.numericov4.fragments.listViewCustomAdapter.Newton;
import com.example.sacrew.numericov4.fragments.listViewCustomAdapter.NewtonListAdapter;
import com.example.sacrew.numericov4.fragments.tableview.TableViewModel;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.github.johnpersano.supertoasts.library.SuperToast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import static com.example.sacrew.numericov4.fragments.homeFragment.poolColors;


/**
 * A simple {@link Fragment} subclass.
 */
public class newtonFragment extends baseOneVariableFragments {

    private Expression functionG;
    private View view;
    private ListView listView;
    private TextView xvalue;
    private AutoCompleteTextView textFunctionG;
    private ToggleButton errorToggle;

    public newtonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            view = inflater.inflate(R.layout.fragment_newton, container, false);
        } catch (InflateException e) {
            // ignorable
        }
        Button runFixed = view.findViewById(R.id.runNewton);
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
        textFunction = view.findViewById(R.id.function);
        iter = view.findViewById(R.id.iterations);
        textError = view.findViewById(R.id.error);
        xvalue = view.findViewById(R.id.xValue);
        textFunctionG = view.findViewById(R.id.functionG);
        errorToggle = view.findViewById(R.id.errorToggle);

        textFunction.setAdapter(new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, graphFragment.allFunctions));
        textFunctionG.setAdapter(new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, graphFragment.allFunctions));
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void executeHelp() {
        Intent i = new Intent(getContext().getApplicationContext(), popUpNewton.class);
        startActivity(i);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void execute(boolean error, double errorValue, int ite) {
        Double xValue = 0.0;

        String originalFuncG = textFunctionG.getText().toString();

        this.functionG = new Expression(functionRevision(originalFuncG));
        error = checkSyntax(originalFuncG, textFunctionG) && error;
        updatefunctions(originalFuncG);
        String originalFunc = this.function.getExpression();

        try {
            xValue = Double.parseDouble(xvalue.getText().toString());
        } catch (Exception e) {
            xvalue.setError("Invalid Xi");
            error = false;
        }
        String functionCompose = "x-((" + originalFunc + ")/(" + originalFuncG + "))";

        if (error) {
            if (errorToggle.isChecked()) {
                newtonMethod(xValue, errorValue, ite, true, functionCompose);
            } else {
                newtonMethod(xValue, errorValue, ite, false, functionCompose);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void newtonMethod(Double x0, Double tol, int ite, boolean errorRel, String functionCompose) {
        String message = "";
        Expression newtonFunction = new Expression(functionCompose);
        try {


            function.setPrecision(100);
            ArrayList<Newton> listValues = new ArrayList<>();
            Newton titles = new Newton("n", "Xn", "f(Xn)", "f'(Xn)", "Error");
            listValues.add(titles);
            listValuesTitles = new LinkedList<>();
            listValuesTitles.add("Xn");
            listValuesTitles.add("f(Xn)");
            listValuesTitles.add("f'(Xn)");
            listValuesTitles.add("Error");
            //TableViewModel.getTitles(listValuesTitles);
            completeList = new LinkedList<>();
            if (tol >= 0) {
                if (ite > 0) {
                    double y0 = (this.function.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    double y0p = (this.function.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    if (y0 != 0) {
                        int cont = 0;
                        double error = tol + 1;
                        Newton iteZero = new Newton(String.valueOf(cont), String.valueOf(convertirNormal(x0)), String.valueOf(convertirNormal(y0)), String.valueOf(convertirNormal(y0p)), String.valueOf(convertirCientifica(error)));
                        listValues.add(iteZero);
                        List<String> listValuesIteZero = new LinkedList<>();
                        listValuesIteZero.add(String.valueOf(x0));
                        listValuesIteZero.add(String.valueOf(y0));
                        listValuesIteZero.add(String.valueOf(y0p));
                        listValuesIteZero.add(String.valueOf(convertirCientifica(error)));
                        double xa = x0;
                        completeList.add(listValuesIteZero);
                        calc= true;
                        while ((y0 != 0) && (error > tol) && (cont < ite)) {
                            ArrayList<String> listValuesIteNext = new ArrayList<String>();
                            double xn = (newtonFunction.with("x", BigDecimal.valueOf(xa)).eval()).doubleValue();
                            y0 = (this.function.with("x", BigDecimal.valueOf(xa)).eval()).doubleValue();

                            if (errorRel)
                                error = Math.abs(xn - xa) / xn;
                            else
                                error = Math.abs(xn - xa);
                            xa = xn;
                            cont++;
                            Newton iteNext = new Newton(String.valueOf(cont), String.valueOf(convertirNormal(xa)), String.valueOf(convertirNormal(y0)), String.valueOf(convertirNormal(y0p)), String.valueOf(convertirCientifica(error)));
                            listValues.add(iteNext);
                            listValuesIteNext.add(String.valueOf(x0));
                            listValuesIteNext.add(String.valueOf(y0));
                            listValuesIteNext.add(String.valueOf(y0p));
                            listValuesIteNext.add(String.valueOf(convertirCientifica(error)));
                            completeList.add(listValuesIteNext);
                        }
                        //TableViewModel.getCeldas(completeList);

                        int color = poolColors.remove(0);
                        poolColors.add(color);
                        graphSerie(function.getExpression(),0,xa*2,color);
                        //graphSerie(xa - 0.5, xa, function.getExpression(), graph, Color.BLUE);
                        if (y0 == 0) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa,y0,color);
                            //graphPoint(xa, y0, PointsGraphSeries.Shape.POINT, graph, getActivity(), Color.parseColor("#0E9577"), true);
                            //Toast.makeText(getContext(), convertirNormal(xa) + " is a root", Toast.LENGTH_SHORT).show();
                            message = convertirNormal(xa) + " is a root";
                            styleCorrectMessage(message);
                        } else if (error <= tol) {
                            color = poolColors.remove(0);
                            poolColors.add(color);
                            graphPoint(xa,y0,color);

                            //graphPoint(xa, y0, PointsGraphSeries.Shape.POINT, graph, getActivity(), Color.parseColor("#0E9577"), true);
                            message = convertirNormal(xa) + " is an aproximate root";
                            styleCorrectMessage(message);
                            // Toast.makeText(getContext(), convertirNormal(xa) + " is an aproximate root", Toast.LENGTH_SHORT).show();
                        } else {
                            //System.out.println("Failed the interval!");
                            message = "Failed the interval!";
                            styleWrongMessage(message);
                            //Toast.makeText(getContext(), "Failed the interval!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        int color = poolColors.remove(0);
                        poolColors.add(color);
                        graphPoint(x0,y0,color);
                        //graphPoint(x0, y0, PointsGraphSeries.Shape.POINT, graph, getActivity(), Color.parseColor("#0E9577"), true);
                        //Toast.makeText(getContext(), convertirNormal(x0) + " is an aproximate root", Toast.LENGTH_SHORT).show();
                        message = convertirNormal(x0) + " is an aproximate root";
                        styleCorrectMessage(message);
                    }
                } else {
                    iter.setError("Wrong iterates");
                    message = "Wrong iterates";
                    styleWrongMessage(message);
                }
            } else {
                textError.setError("Tolerance must be > 0");
                message = "Tolerance must be > 0";
                styleWrongMessage(message);
            }
            NewtonListAdapter adapter = new NewtonListAdapter(getContext(), R.layout.list_adapter_newton, listValues);
            listView.setAdapter(adapter);
        } catch (Exception e) {
           // Toast.makeText(getActivity(), "Unexpected error posibly nan", Toast.LENGTH_SHORT).show();
            message = "Unexpected error posibly nan";
            styleWrongMessage(message);
        }
    }

    public void updatefunctions(String function) {
        if (!graphFragment.allFunctions.contains(function)) {
            graphFragment.allFunctions.add(function);
            textFunction.setAdapter(new ArrayAdapter<String>
                    (getActivity(), android.R.layout.select_dialog_item, graphFragment.allFunctions));
            textFunctionG.setAdapter(new ArrayAdapter<String>
                    (getActivity(), android.R.layout.select_dialog_item, graphFragment.allFunctions));
        }
    }

}




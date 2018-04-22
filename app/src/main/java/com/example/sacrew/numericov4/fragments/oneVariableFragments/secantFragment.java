package com.example.sacrew.numericov4.fragments.oneVariableFragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.sacrew.numericov4.R;
import com.example.sacrew.numericov4.fragments.customPopUps.popUpFixedPoint;
import com.example.sacrew.numericov4.fragments.customPopUps.popUpSecant;
import com.example.sacrew.numericov4.fragments.home;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.udojava.evalex.Expression;

import java.math.BigDecimal;

import static com.example.sacrew.numericov4.graphMethods.functionRevision;
import static com.example.sacrew.numericov4.graphMethods.graphPoint;
import static com.example.sacrew.numericov4.graphMethods.graphSerie;

/**
 * A simple {@link Fragment} subclass.
 */
public class secantFragment extends Fragment {

    private Button runSecant;
    private Button runHelp;
    private GraphView graph;
    private Expression function,functionG;
    private View view;
    private TextView xi,xs,iter,textError;
    private AutoCompleteTextView textFunction;
    private ToggleButton errorToggle;
    public secantFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_secant, container, false);
        runSecant = view.findViewById(R.id.runSecant);
        runSecant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execute();
            }
        });
        runHelp = view.findViewById(R.id.runHelp);
        runHelp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                executeHelp();
            }
        });
        graph = view.findViewById(R.id.secantGraph);
        textFunction = view.findViewById(R.id.function);
        iter = view.findViewById(R.id.iterations);
        textError = view.findViewById(R.id.error);
        xi = view.findViewById(R.id.xi);
        xs = view.findViewById(R.id.xs);
        errorToggle = view.findViewById(R.id.errorToggle);

        textFunction.setAdapter(new ArrayAdapter<String>
                (getActivity(), android.R.layout.select_dialog_item, home.allFunctions));
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void executeHelp(){
        Intent i = new Intent(getContext().getApplicationContext(), popUpSecant.class);
        startActivity(i);
    }

    public void execute() {
        boolean error = false;
        Double xiValue = 0.0;
        Double xsValue = 0.0;
        int ite = 0;
        Double errorValue = 0.0;
        String originalFunc = textFunction.getText().toString();
        try {

            this.function = new Expression(functionRevision(originalFunc));

            (function.with("x", BigDecimal.valueOf(1)).eval()).doubleValue();
            if (!home.allFunctions.contains(originalFunc)) {
                home.allFunctions.add(originalFunc);
                textFunction.setAdapter(new ArrayAdapter<String>
                        (getActivity(), android.R.layout.select_dialog_item, home.allFunctions));
            }

        } catch (Exception e) {
            textFunction.setError("Invalid function");

            error = true;
        }
        try {
            xiValue = Double.parseDouble(xi.getText().toString());
            (function.with("x", BigDecimal.valueOf(xiValue)).eval()).doubleValue();
        } catch (Exception e) {
            xi.setError("Invalid Xi");
            error = true;
        }
        try {
            xsValue = Double.parseDouble(xs.getText().toString());
            (function.with("x", BigDecimal.valueOf(xsValue)).eval()).doubleValue();
        } catch (Exception e) {
            xs.setError("Invalid xs");
            error = true;
        }
        try {
            ite = Integer.parseInt(iter.getText().toString());
        } catch (Exception e) {
            iter.setError("Wrong iterations");
            error = true;
        }
        try {
            errorValue = new Expression(textError.getText().toString()).eval().doubleValue();
            System.out.println("error value  " + errorValue);
        } catch (Exception e) {
            textError.setError("Invalid error value");
        }
        if (!error) {
            if (errorToggle.isChecked()) {
                secantMethod(xiValue, xsValue, errorValue, ite, true);
            } else {
                secantMethod(xiValue, xsValue, errorValue, ite, false);
            }
        }
    }

    private void secantMethod(Double x0,Double x1, Double tol, int ite, boolean errorRel) {
        try {
            graph.removeAllSeries();

            function.setPrecision(100);
            if (tol >= 0) {
                if (ite > 0) {
                    double fx0 = (this.function.with("x", BigDecimal.valueOf(x0)).eval()).doubleValue();
                    if (fx0 != 0) {
                        Double fx1 = (this.function.with("x", BigDecimal.valueOf(x1)).eval()).doubleValue();

                        Double error = tol+1;
                        int cont = 0;
                        Double aux0 = x0;
                        Double aux1 = x1;
                        Double den = fx1-fx0;
                        while(fx1 != 0 && den != 0 && error > tol && cont < ite) {
                            Double aux2 = aux1 - (((this.function.with("x", BigDecimal.valueOf(aux1))
                                    .eval().doubleValue()))* (aux1 - aux0) / den);
                            if (errorRel)
                                error = Math.abs(aux2 - aux1) / aux2;
                            else
                                error = Math.abs(aux2 - aux1);
                            aux0 = aux1;
                            fx0 = fx1;
                            aux1 = aux2;
                            fx1  = (this.function.with("x", BigDecimal.valueOf(aux1)).eval()).doubleValue();
                            den = fx1 - fx0;
                            cont = cont + 1;
                        }
                        graphSerie(aux1-0.5, aux1, function.getExpression(), graph, Color.BLUE);
                        if (fx1 == 0) {
                            graphPoint(aux1, fx1, PointsGraphSeries.Shape.POINT, graph, getActivity(), "#0E9577", true);
                            //System.out.println(xa + " is a root");
                        } else if (error <= tol) {
                            fx1 = (this.function.with("x", BigDecimal.valueOf(aux1)).eval()).doubleValue();
                            graphPoint(aux1, fx1, PointsGraphSeries.Shape.POINT, graph, getActivity(), "#0E9577", true);
                            //System.out.println(xa + " is an aproximate root");
                        } else {
                            System.out.println("Failed the interval!");
                        }
                    } else {
                        graphPoint(x0, fx0, PointsGraphSeries.Shape.POINT, graph, getActivity(), "#0E9577", true);
                        //System.out.println(x0 + " is a root");
                    }
                } else {
                    iter.setError("Wrong iterates");
                    //System.out.println("Wrong iterates!");
                }
            } else {
                textError.setError("Tolerance must be < 0");
                //System.out.println("Tolerance < 0");
            }
        }catch(Exception e){
            Toast.makeText(getActivity(), "Unexpected error posibly nan", Toast.LENGTH_SHORT).show();
        }
    }

}
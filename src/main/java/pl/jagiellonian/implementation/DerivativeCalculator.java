package pl.jagiellonian.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import pl.jagiellonian.exceptions.WrongDifferentiationVariable;
import pl.jagiellonian.interfaces.ITreeExpression;
import pl.jagiellonian.interfaces.IVariable;
import pl.jagiellonian.interfaces.Sorter;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class DerivativeCalculator {
    private Sorter<IVariable> sorter;

    public DerivativeCalculator(Sorter<IVariable> sorter) {
        this.sorter = sorter;
    }

    /**
     * @param monomial a monomial to differentiate
     * @param variable chosen variable to differentiate
     * @return {@link IVariable} or {@link ITreeExpression} derivative of monomial by variable
     */
    public IVariable differentiate(IVariable monomial, IVariable variable) {
        if (variable.isExpression() || isNumber(variable)) {
            throw new WrongDifferentiationVariable();
        }
        if (!monomial.isExpression()) {
            if (variable.toString().equals(monomial.toString())) {
                return new Variable("1");
            }
            return new Variable("0");
        }
        ITreeExpression expression = (ITreeExpression) monomial;
        List<IVariable> nodes = expression.getAllNodes();
        for (IVariable var : nodes) {
            if (var.toString().equals(variable.toString())) {
                long exponent = nodes.stream().collect(groupingBy(IVariable::toString, counting())).get(var.toString());
                nodes.remove(var);
                if (exponent != 1) {
                    nodes.add(new Variable(Long.toString(exponent)));
                }
                return sorter.sort(getVariablesMultiplication(nodes));
            }
        }
        return new Variable("0");
    }

    private IVariable getVariablesMultiplication(List<IVariable> nodesPowered) {
        IVariable result = new ArrayList<>(nodesPowered).get(0);
        for (int i = 1; i < nodesPowered.size(); i++) {
            result = result.mult(nodesPowered.get(i));
        }
        return result;
    }

    private boolean isNumber(IVariable variable) {
        return Pattern.compile("\\d+").matcher(variable.toString()).matches();
    }
}

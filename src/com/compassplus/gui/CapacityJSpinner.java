package com.compassplus.gui;

import com.compassplus.configurationModel.Module;
import com.compassplus.configurationModel.Product;
import com.compassplus.configurationModel.RequireCapacity;
import com.compassplus.proposalModel.Capacity;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.geom.Line2D;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 10/14/11
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CapacityJSpinner extends JSpinner {
    private String key;

    private class CapacitySpinnerNumberModel extends SpinnerNumberModel {
        private Number stepSize, value;
        private Comparable minimum, maximum;
        private Boolean isDeprecated;
        private Component parent;

        private String key;
        private ProductForm form;
        private JLabel label;
        private ArrayList<Integer> mins = new ArrayList<Integer>(0);
        private Integer incrs;
        private Integer focs;
        private Integer user;

        /**
         * Constructs a <code>SpinnerModel</code> that represents
         * a closed sequence of
         * numbers from <code>minimum</code> to <code>maximum</code>.  The
         * <code>nextValue</code> and <code>previousValue</code> methods
         * compute elements of the sequence by adding or subtracting
         * <code>stepSize</code> respectively.  All of the parameters
         * must be mutually <code>Comparable</code>, <code>value</code>
         * and <code>stepSize</code> must be instances of <code>Integer</code>
         * <code>Long</code>, <code>Float</code>, or <code>Double</code>.
         * <p/>
         * The <code>minimum</code> and <code>maximum</code> parameters
         * can be <code>null</code> to indicate that the range doesn't
         * have an upper or lower bound.
         * If <code>value</code> or <code>stepSize</code> is <code>null</code>,
         * or if both <code>minimum</code> and <code>maximum</code>
         * are specified and <code>mininum &gt; maximum</code> then an
         * <code>IllegalArgumentException</code> is thrown.
         * Similarly if <code>(minimum &lt;= value &lt;= maximum</code>) is false,
         * an <code>IllegalArgumentException</code> is thrown.
         *
         * @param value    the current (non <code>null</code>) value of the model
         * @param minimum  the first number in the sequence or <code>null</code>
         * @param maximum  the last number in the sequence or <code>null</code>
         * @param stepSize the difference between elements of the sequence
         * @throws IllegalArgumentException if stepSize or value is
         *                                  <code>null</code> or if the following expression is false:
         *                                  <code>minimum &lt;= value &lt;= maximum</code>
         */
        public CapacitySpinnerNumberModel(Component parent, boolean isDeprecated, Capacity value, Comparable minimum, Comparable maximum, Number stepSize, String key, ProductForm form, JLabel label) {
            this.isDeprecated = isDeprecated;
            this.parent = parent;
            /*if ((value == null) || (stepSize == null)) {
                throw new IllegalArgumentException("value and stepSize must be non-null");
            }
            if (!(((minimum == null) || (minimum.compareTo(value) <= 0)) &&
                    ((maximum == null) || (maximum.compareTo(value) >= 0)))) {
                throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
            }*/
            this.minimum = minimum;
            this.maximum = maximum;
            this.stepSize = stepSize;
            this.form = form;
            this.label = label;
            this.key = key;
            this.mins.add(new Integer(0));
            if (value != null) {
                this.incrs = value.getIncr();
                this.focs = value.getFoc();
                this.user = value.getUser();
                for (com.compassplus.proposalModel.Module mm : form.getProduct().getModules().values()) {
                    Module m = form.getProduct().getProduct().getModules().get(mm.getKey());
                    if (m.getRequireCapacities().containsKey(key) && !m.getRequireCapacities().get(key).isIncremental()) {
                        this.mins.add(m.getRequireCapacities().get(key).getValue());
                    }
                }
            } else {
                this.incrs = 0;
                this.focs = 0;
                this.user = 0;
            }
            recalc();
        }

        public void recalc() {
            Integer ret = this.incrs + this.user;
            Integer maxmin = mins.get(0);
            for (Integer i : mins) {
                if (maxmin < i) {
                    maxmin = i;
                }
            }
            if (ret < maxmin) {
                ret = maxmin;
            }
            //this.minimum = this.incrs > maxmin ? this.incrs : maxmin;
            this.value = this.user;
            if (ret > 0) {
                if (!form.getProduct().getCapacities().containsKey(key)) {
                    com.compassplus.configurationModel.Capacity tmpCapacity = form.getProduct().getProduct().getCapacities().get(key);
                    form.getProduct().addCapacity(tmpCapacity, key);
                }
                Capacity tmpCapacity = form.getProduct().getCapacities().get(key);
                tmpCapacity.setUser(this.user);
                tmpCapacity.setMin(maxmin);
                tmpCapacity.setIncr(this.incrs);
                tmpCapacity.setFoc(this.focs);
            } else {
                form.getProduct().delCapacity(key);
            }
            fireStateChanged();
        }

        /**
         * Constructs a <code>SpinnerNumberModel</code> with the specified
         * <code>value</code>, <code>minimum</code>/<code>maximum</code> bounds,
         * and <code>stepSize</code>.
         *
         * @param value    the current value of the model
         * @param minimum  the first number in the sequence
         * @param maximum  the last number in the sequence
         * @param stepSize the difference between elements of the sequence
         * @throws IllegalArgumentException if the following expression is false:
         *                                  <code>minimum &lt;= value &lt;= maximum</code>
         */
        public CapacitySpinnerNumberModel(Component parent, boolean isDeprecated, Capacity value, int minimum, int maximum, int stepSize, String key, ProductForm form, JLabel label) {
            this(parent, isDeprecated, value, new Integer(minimum), new Integer(maximum), new Integer(stepSize), key, form, label);
        }


        /**
         * Changes the lower bound for numbers in this sequence.
         * If <code>minimum</code> is <code>null</code>,
         * then there is no lower bound.  No bounds checking is done here;
         * the new <code>minimum</code> value may invalidate the
         * <code>(minimum &lt;= value &lt= maximum)</code>
         * invariant enforced by the constructors.  This is to simplify updating
         * the model, naturally one should ensure that the invariant is true
         * before calling the <code>getNextValue</code>,
         * <code>getPreviousValue</code>, or <code>setValue</code> methods.
         * <p/>
         * Typically this property is a <code>Number</code> of the same type
         * as the <code>value</code> however it's possible to use any
         * <code>Comparable</code> with a <code>compareTo</code>
         * method for a <code>Number</code> with the same type as the value.
         * For example if value was a <code>Long</code>,
         * <code>minimum</code> might be a Date subclass defined like this:
         * <pre>
         * MyDate extends Date {  // Date already implements Comparable
         *     public int compareTo(Long o) {
         *         long t = getTime();
         *         return (t < o.longValue() ? -1 : (t == o.longValue() ? 0 : 1));
         *     }
         * }
         * </pre>
         * <p/>
         * This method fires a <code>ChangeEvent</code>
         * if the <code>minimum</code> has changed.
         *
         * @param minimum a <code>Comparable</code> that has a
         *                <code>compareTo</code> method for <code>Number</code>s with
         *                the same type as <code>value</code>
         * @see #getMinimum
         * @see #setMaximum
         * @see SpinnerModel#addChangeListener
         */
        public void setMinimum(Comparable minimum) {
            if ((minimum == null) ? (this.minimum != null) : !minimum.equals(this.minimum)) {
                this.minimum = minimum;
                fireStateChanged();
            }
        }


        /**
         * Returns the first number in this sequence.
         *
         * @return the value of the <code>minimum</code> property
         * @see #setMinimum
         */
        public Comparable getMinimum() {
            return minimum;
        }


        /**
         * Changes the upper bound for numbers in this sequence.
         * If <code>maximum</code> is <code>null</code>, then there
         * is no upper bound.  No bounds checking is done here; the new
         * <code>maximum</code> value may invalidate the
         * <code>(minimum <= value < maximum)</code>
         * invariant enforced by the constructors.  This is to simplify updating
         * the model, naturally one should ensure that the invariant is true
         * before calling the <code>next</code>, <code>previous</code>,
         * or <code>setValue</code> methods.
         * <p/>
         * Typically this property is a <code>Number</code> of the same type
         * as the <code>value</code> however it's possible to use any
         * <code>Comparable</code> with a <code>compareTo</code>
         * method for a <code>Number</code> with the same type as the value.
         * See <a href="#setMinimum(java.lang.Comparable)">
         * <code>setMinimum</code></a> for an example.
         * <p/>
         * This method fires a <code>ChangeEvent</code> if the
         * <code>maximum</code> has changed.
         *
         * @param maximum a <code>Comparable</code> that has a
         *                <code>compareTo</code> method for <code>Number</code>s with
         *                the same type as <code>value</code>
         * @see #getMaximum
         * @see #setMinimum
         * @see SpinnerModel#addChangeListener
         */
        public void setMaximum(Comparable maximum) {
            if ((maximum == null) ? (this.maximum != null) : !maximum.equals(this.maximum)) {
                this.maximum = maximum;
                fireStateChanged();
            }
        }


        /**
         * Returns the last number in the sequence.
         *
         * @return the value of the <code>maximum</code> property
         * @see #setMaximum
         */
        public Comparable getMaximum() {
            return maximum;
        }


        /**
         * Changes the size of the value change computed by the
         * <code>getNextValue</code> and <code>getPreviousValue</code>
         * methods.  An <code>IllegalArgumentException</code>
         * is thrown if <code>stepSize</code> is <code>null</code>.
         * <p/>
         * This method fires a <code>ChangeEvent</code> if the
         * <code>stepSize</code> has changed.
         *
         * @param stepSize the size of the value change computed by the
         *                 <code>getNextValue</code> and <code>getPreviousValue</code> methods
         * @see #getNextValue
         * @see #getPreviousValue
         * @see #getStepSize
         * @see SpinnerModel#addChangeListener
         */
        public void setStepSize(Number stepSize) {
            if (stepSize == null) {
                throw new IllegalArgumentException("null stepSize");
            }
            if (!stepSize.equals(this.stepSize)) {
                this.stepSize = stepSize;
                fireStateChanged();
            }
        }


        /**
         * Returns the size of the value change computed by the
         * <code>getNextValue</code>
         * and <code>getPreviousValue</code> methods.
         *
         * @return the value of the <code>stepSize</code> property
         * @see #setStepSize
         */
        public Number getStepSize() {
            return stepSize;
        }


        private Number incrValue(int dir) {
            Number newValue;
            if ((value instanceof Float) || (value instanceof Double)) {
                double v = value.doubleValue() + (stepSize.doubleValue() * (double) dir);
                if (value instanceof Double) {
                    newValue = new Double(v);
                } else {
                    newValue = new Float(v);
                }
            } else {
                long v = value.longValue() + (stepSize.longValue() * (long) dir);

                if (value instanceof Long) {
                    newValue = new Long(v);
                } else if (value instanceof Integer) {
                    newValue = new Integer((int) v);
                } else if (value instanceof Short) {
                    newValue = new Short((short) v);
                } else {
                    newValue = new Byte((byte) v);
                }
            }

            if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
                return null;
            }
            if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
                return null;
            } else {
                return newValue;
            }
        }


        /**
         * Returns the next number in the sequence.
         *
         * @return <code>value + stepSize</code> or <code>null</code> if the sum
         *         exceeds <code>maximum</code>.
         * @see SpinnerModel#getNextValue
         * @see #getPreviousValue
         * @see #setStepSize
         */
        public Object getNextValue() {
            return incrValue(+1);
        }


        /**
         * Returns the previous number in the sequence.
         *
         * @return <code>value - stepSize</code>, or
         *         <code>null</code> if the sum is less
         *         than <code>minimum</code>.
         * @see SpinnerModel#getPreviousValue
         * @see #getNextValue
         * @see #setStepSize
         */
        public Object getPreviousValue() {
            return incrValue(-1);
        }


        /**
         * Returns the value of the current element of the sequence.
         *
         * @return the value property
         * @see #setValue
         */
        public Number getNumber() {
            return value;
        }


        /**
         * Returns the value of the current element of the sequence.
         *
         * @return the value property
         * @see #setValue
         * @see #getNumber
         */
        public Object getValue() {
            return value;
        }

        /**
         * Sets the current value for this sequence.  If <code>value</code> is
         * <code>null</code>, or not a <code>Number</code>, an
         * <code>IllegalArgumentException</code> is thrown.  No
         * bounds checking is done here; the new value may invalidate the
         * <code>(minimum &lt;= value &lt;= maximum)</code>
         * invariant enforced by the constructors.   It's also possible to set
         * the value to be something that wouldn't naturally occur in the sequence,
         * i.e. a value that's not modulo the <code>stepSize</code>.
         * This is to simplify updating the model, and to accommodate
         * spinners that don't want to restrict values that have been
         * directly entered by the user. Naturally, one should ensure that the
         * <code>(minimum &lt;= value &lt;= maximum)</code> invariant is true
         * before calling the <code>next</code>, <code>previous</code>, or
         * <code>setValue</code> methods.
         * <p/>
         * This method fires a <code>ChangeEvent</code> if the value has changed.
         *
         * @param value the current (non <code>null</code>) <code>Number</code>
         *              for this sequence
         * @throws IllegalArgumentException if <code>value</code> is
         *                                  <code>null</code> or not a <code>Number</code>
         * @see #getNumber
         * @see #getValue
         * @see SpinnerModel#addChangeListener
         */
        public void setValue(Object value) {
            if ((value == null) || !(value instanceof Number) || isDeprecated && !((Integer) value).equals(new Integer(0))) {
                if (!value.equals(this.value)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            JOptionPane.showMessageDialog(parent, "Deprecated capacity can't be used. You should set \"0\" here.", "Error", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                }
                throw new IllegalArgumentException("illegal value");
            }
            if (!value.equals(this.value)) {
                setUser((Integer) value);
            }
        }

        public void addMin(Integer min) {
            mins.add(min);
            recalc();
        }

        public void delMin(Integer min) {
            mins.remove(min);
            recalc();
        }

        public void addIncr(Integer incr) {
            incrs += incr;
            recalc();
        }

        public void delIncr(Integer incr) {
            incrs -= incr;
            recalc();
        }

        public void addFoc(Integer foc) {
            focs += foc;
            recalc();
        }

        public void delFoc(Integer foc) {
            focs -= foc;
            recalc();
        }

        public void setUser(Integer user) {
            this.user = user;
            recalc();
        }
    }


    public void addMin(Integer min) {
        ((CapacitySpinnerNumberModel) this.getModel()).addMin(min);
    }

    public void delMin(Integer min) {
        ((CapacitySpinnerNumberModel) this.getModel()).delMin(min);
    }

    public void addIncr(Integer incr) {
        ((CapacitySpinnerNumberModel) this.getModel()).addIncr(incr);
    }

    public void delIncr(Integer incr) {
        ((CapacitySpinnerNumberModel) this.getModel()).delIncr(incr);
    }

    public void addFoc(Integer foc) {
        ((CapacitySpinnerNumberModel) this.getModel()).addFoc(foc);
    }

    public void delFoc(Integer foc) {
        ((CapacitySpinnerNumberModel) this.getModel()).delFoc(foc);
    }

    public void recalc() {
        ((CapacitySpinnerNumberModel) this.getModel()).recalc();
    }

    public CapacityJSpinner(Capacity initialCapacity, boolean isDeprecated, String key, ProductForm form, JLabel label) {
        super();
        setModel(new CapacitySpinnerNumberModel(form.getRoot(), isDeprecated, initialCapacity, 0, Integer.MAX_VALUE, 1, key, form, label));
        JFormattedTextField tf = ((JSpinner.DefaultEditor) this.getEditor()).getTextField();
        DefaultFormatterFactory formatterFactory = (DefaultFormatterFactory) tf.getFormatterFactory();
        DecimalFormat df = new DecimalFormat();
        ((NumberFormatter) formatterFactory.getDefaultFormatter()).setFormat(df);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

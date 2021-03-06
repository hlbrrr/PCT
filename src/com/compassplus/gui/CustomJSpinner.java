package com.compassplus.gui;

import com.compassplus.proposalModel.Product;
import com.compassplus.proposalModel.Proposal;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: arudin
 * Date: 12/23/11
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomJSpinner extends JSpinner {
    private class CustomSpinnerNumberModel extends SpinnerNumberModel {
        private Number stepSize, value;
        private Comparable minimum, maximum, maximumDiscount;
        private Component parent;
        private String message = "";
        private Product product;
        private boolean isSupport;

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
        public CustomSpinnerNumberModel(String message, Component parent, Number value, Comparable minimum, Comparable maximum, Number stepSize, Product product, boolean isSupport) {
            if (message != null) {
                this.message = message;
            }
            if ((value == null) || (stepSize == null)) {
                throw new IllegalArgumentException("value and stepSize must be non-null");
            }
            if (!(((minimum == null) || (minimum.compareTo(value) <= 0)) &&
                    ((maximum == null) || (maximum.compareTo(value) >= 0)))) {
                //throw new IllegalArgumentException("(minimum <= value <= maximum) is false");
            }
            this.value = value;
            this.minimum = minimum;
            this.maximumDiscount = 100d;
            this.stepSize = stepSize;
            this.product = product;
            this.isSupport = isSupport;
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
        public CustomSpinnerNumberModel(String message, Component parent, int value, int minimum, int maximum, int stepSize, Product product, boolean isSupport) {
            this(message, parent, new Integer(value), new Integer(minimum), new Integer(maximum), new Integer(stepSize), product, isSupport);
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
        public CustomSpinnerNumberModel(String message, Component parent, double value, double minimum, double maximum, double stepSize, Product product, boolean isSupport) {
            this(message, parent, new Double(value), new Double(minimum), new Double(maximum), new Double(stepSize), product, isSupport);
        }


        /**
         * Constructs a <code>SpinnerNumberModel</code> with no
         * <code>minimum</code> or <code>maximum</code> value,
         * <code>stepSize</code> equal to one, and an initial value of zero.
         */
        public CustomSpinnerNumberModel(String message, Component parent, Product product, boolean isSupport) {
            this(message, parent, new Integer(0), null, null, new Integer(1), product, isSupport);
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
        public void setValue(Object value, boolean allowException) {
            if ((value == null) || !(value instanceof Number)) {
                throw new IllegalArgumentException("illegal value");
            }
            Double maximumDiscountedSum = (1 - ((Number) maximumDiscount).doubleValue() / 100d) * (isSupport ? product.getSupportPriceUndiscounted(true) : product.getRegionPrice(true));

            final Double newMax = (Math.round(10000d * (1 - maximumDiscountedSum / (isSupport ? product.getSupportPriceUndiscounted() : product.getRegionPrice()))) / 100d);

            //if (!value.equals(this.value)) {
            if (newMax.compareTo(((Number) value).doubleValue()) < 0) {
                /*SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(parent, message + newMax.toString() + "%.", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                });*/

                this.value = (Number) newMax;
                if(allowException){
                    throw new IllegalArgumentException("illegal value");
                }
                fireStateChanged();
            } else {
                this.value = (Number) value;
                fireStateChanged();
            }
            //}
        }

        public void setValue(Object value) {
            setValue(value, false);
        }
    }

    public CustomJSpinner(String message, Component parent, int value, int minimum, int maximum, int stepSize, Product product, boolean isSupport) {
        super();
        setModel(new CustomSpinnerNumberModel(message, parent, value, minimum, maximum, stepSize, product, isSupport));
    }

    public CustomJSpinner(String message, Component parent, double value, double minimum, double maximum, double stepSize, Product product, boolean isSupport) {
        super();
        setModel(new CustomSpinnerNumberModel(message, parent, value, minimum, maximum, stepSize, product, isSupport));
    }

    public void setValue(Object value){
        ((CustomSpinnerNumberModel)this.getModel()).setValue(value, true);
    }
}

/*
 * #%L
 * ImageJ software for multidimensional image processing and analysis.
 * %%
 * Copyright (C) 2009 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package imagej.ext.module.ui;

import imagej.ext.module.Module;
import imagej.ext.module.ModuleItem;
import imagej.util.ClassUtils;
import imagej.util.NumberUtils;

import java.util.List;

/**
 * The backing data model for a particular {@link InputWidget}.
 * 
 * @author Curtis Rueden
 */
public class WidgetModel {

	private final InputPanel inputPanel;
	private final Module module;
	private final ModuleItem<?> item;

	private final String widgetLabel;

	private boolean initialized;

	public WidgetModel(final InputPanel inputPanel, final Module module,
		final ModuleItem<?> item)
	{
		this.inputPanel = inputPanel;
		this.module = module;
		this.item = item;

		widgetLabel = makeWidgetLabel(item.getLabel());
	}

	public Module getModule() {
		return module;
	}

	public ModuleItem<?> getItem() {
		return item;
	}

	public String getWidgetLabel() {
		return widgetLabel;
	}

	public Object getValue() {
		return item.getValue(module);
	}

	public void setValue(final Object value) {
		final String name = item.getName();
		if (objectsEqual(module.getInput(name), value)) return; // no change
		module.setInput(name, value);
		if (initialized) {
			item.callback(module);
			inputPanel.refresh();
			module.preview();
		}
	}

	public boolean isCompatibleWith(final Class<?> type) {
		return ClassUtils.canConvert(getItem().getType(), type);
	}

	public Number getMin() {
		final Class<?> type = item.getType();
		final Class<?> saneType = ClassUtils.getNonprimitiveType(type);
		final Object itemMin = item.getMinimumValue();
		final Number min = NumberUtils.toNumber(itemMin, saneType);
		if (min != null) return min;
		return NumberUtils.getMinimumNumber(type);
	}

	public Number getMax() {
		final Class<?> type = item.getType();
		final Class<?> saneType = ClassUtils.getNonprimitiveType(type);
		final Object itemMax = item.getMaximumValue();
		final Number max = NumberUtils.toNumber(itemMax, saneType);
		if (max != null) return max;
		return NumberUtils.getMaximumNumber(type);
	}

	public Number getStepSize() {
		final Class<?> type = item.getType();
		final Class<?> saneType = ClassUtils.getNonprimitiveType(type);
		final Object itemStep = item.getStepSize();
		final Number stepSize = NumberUtils.toNumber(itemStep, saneType);
		if (stepSize != null) return stepSize;
		return NumberUtils.toNumber("1", type);
	}

	public String[] getChoices() {
		final List<?> choicesList = item.getChoices();
		final String[] choices = new String[choicesList.size()];
		for (int i = 0; i < choices.length; i++) {
			choices[i] = choicesList.get(i).toString();
		}
		return choices;
	}

	public void setInitialized(final boolean initialized) {
		this.initialized = initialized;
	}

	public boolean isInitialized() {
		return initialized;
	}

	// -- Helper methods --

	private String makeWidgetLabel(final String s) {
		if (s == null || s.isEmpty()) {
			final String name = item.getName();
			return name.substring(0, 1).toUpperCase() + name.substring(1);
		}
		return s;
	}

	private boolean objectsEqual(final Object obj1, final Object obj2) {
		if (obj1 == null) return obj2 == null;
		return obj1.equals(obj2);
	}
}

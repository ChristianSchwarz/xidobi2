/*
 * Copyright Gemtec GmbH 2009-2013
 *
 * Erstellt am: 06.09.2013 14:25:35
 * Erstellt von: Peter-René Jeschke
 */
package org.xidobi.rfc2217.internal.commands;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.io.DataOutput;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Tests the class {@link AbstractControlCmd}.
 * 
 * @author Peter-René Jeschke
 */
public class TestAbstractControlCommand {

	private class AbstractControlCmdImpl extends AbstractControlCmd {

		/**
		 * @param commandCode
		 */
		AbstractControlCmdImpl(int commandCode) {
			super(commandCode);
		}

		@Override
		public void write(DataOutput output) throws IOException {
		}

	}

	@Rule
	public ExpectedException exception = none();

	/**
	 * When the commandCode is smaller than 0, an {@link IllegalArgumentException} is expected.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_negativeCommandCode() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: -1");
		new AbstractControlCmdImpl(-1);
	}

	/**
	 * When the commandCode is between [0..12], no exception should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_commandCodeBetween0and12() {
		for (int i = 0; i <= 12; i++) {
			new AbstractControlCmdImpl(i);
		}
	}

	/**
	 * When the commandCode is between 12 and 100, an {@link IllegalArgumentException} should be
	 * thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_invalidCommandCode() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: 90");
		new AbstractControlCmdImpl(90);
	}

	/**
	 * When the commandCode is between 100 and 112, no exception should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_commandCodeBetween100And112() {
		for (int i = 100; i <= 112; i++) {
			new AbstractControlCmdImpl(i);
		}
	}

	/**
	 * When the commandCode is too high, an {@link IllegalArgumentException} should be thrown.
	 */
	@SuppressWarnings("unused")
	@Test
	public void new_commandCodeTooHigh() {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("The command code must be in the range [0..12] or [100..112]! Got: 150");
		new AbstractControlCmdImpl(150);
	}

	/**
	 * Verifies that {@link AbstractControlCmd#getCommandCode()} returns the supplied commandCode.
	 */
	@Test
	public void getCommandCode() {
		byte result = new AbstractControlCmdImpl(10).getCommandCode();
		assertThat(result, is((byte) 10));
	}
}

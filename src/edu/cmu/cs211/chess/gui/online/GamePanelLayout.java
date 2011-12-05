package edu.cmu.cs211.chess.gui.online;
//package com.chessclub.easychess;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

public final class GamePanelLayout implements LayoutManager {
	private int m_width = 0, m_height = 0;

	private Component m_board = null, m_uppername = null,
			m_lowername = null, m_upperclock = null, m_lowerclock = null,
			m_button1 = null, m_button2 = null;

	public GamePanelLayout(int w, int h) {
		m_width = w;
		m_height = h;
	}

	// LayoutManager Methods

	public void addLayoutComponent(String name, Component comp) {
		//if ("Scrollbar".equals(name)) {
			//m_scrollbar = comp;
		//} else 
		if ("Board".equals(name)) {
			m_board = comp;
		} else if ("UpperName".equals(name)) {
			m_uppername = comp;
		} else if ("LowerName".equals(name)) {
			m_lowername = comp;
		} else if ("UpperClock".equals(name)) {
			m_upperclock = comp;
		} else if ("LowerClock".equals(name)) {
			m_lowerclock = comp;
		} else if ("Button1".equals(name)) {
			m_button1 = comp;
		} else if ("Button2".equals(name)) {
			m_button2 = comp;

		} else {
			throw new RuntimeException(
					"GamePanelLayout: Unsupported component type \"" + name
							+ "\"");
		}
	}

	public void removeLayoutComponent(Component comp) {
		if (comp == m_button1)
			m_button1 = null;
		else if (comp == m_button2)
			m_button2 = null;
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(m_width, m_height);
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(m_width, m_height);
	}

	public void layoutContainer(Container parent) {
		Insets insets = new Insets(2, 2, 2, 2); // parent.insets();
		Dimension dim = null;
		int top, bottom;
		int vpad = 2, hpad = 2;

		m_width = parent.getSize().width;
		m_height = parent.getSize().height;

		top = insets.top;
		bottom = m_height - insets.bottom;

		if (m_board != null && m_uppername != null && m_lowername != null
				&& m_upperclock != null && m_lowerclock != null) {

			int len1 = bottom - top; // dimension of board
			int len2 = m_width - insets.left - insets.right; // width of
			// leftover

			len1 -= len1 % 8;
			m_board.setBounds(insets.left, top + (bottom - top - len1) / 2, len1,
					len1);

			len2 = m_width - len1 - insets.left - insets.right - hpad;
			int left = insets.left + len1 + hpad;
			/*
			 * // place scrollbar in middle dim=m_scrollbar.preferredSize();
			 * m_scrollbar.reshape(left, (m_height-dim.height)/2 , len2,
			 * dim.height);
			 */
			// place name labels
			dim = m_uppername.getPreferredSize();
			m_uppername.setBounds(left, top, len2, dim.height);
			top += dim.height + vpad;

			dim = m_lowername.getPreferredSize();
			m_lowername.setBounds(left, bottom - dim.height, len2, dim.height);
			bottom -= dim.height + vpad;

			// place clocks
			dim = m_upperclock.getPreferredSize();
			m_upperclock.setBounds(left, top, len2, dim.height);
			top += dim.height + vpad;

			dim = m_lowerclock.getPreferredSize();
			m_lowerclock.setBounds(left, bottom - dim.height, len2, dim.height);
			bottom -= dim.height + vpad;

			int midline = (top + bottom) / 2;
			if (m_button1 != null) {
				dim = m_button1.getPreferredSize();
				m_button1.setBounds(left, midline - dim.height, len2, dim.height);
			}
			if (m_button2 != null) {
				dim = m_button2.getPreferredSize();
				m_button2.setBounds(left, midline, len2, dim.height);
			}
		}
	}
}
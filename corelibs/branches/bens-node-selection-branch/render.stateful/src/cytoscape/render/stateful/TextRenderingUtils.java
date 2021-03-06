
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.render.stateful;

import cytoscape.render.immed.GraphGraphics;

import java.awt.Font;
import java.awt.Paint;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import java.util.StringTokenizer;


final class TextRenderingUtils {
	// No constructor.
	private TextRenderingUtils() {
	}

	/**
	 * @param text potentially multi-line text.
	 */
	public final static void computeTextDimensions(final GraphGraphics grafx, final String text,
	                                               final Font font, final double fontScaleFactor,
	                                               final boolean textAsShape,
	                                               final float[] rtrnVal2x) {
		final StringTokenizer tokenizer = new StringTokenizer(text, "\n");
		double width = 0.0d;
		double height = 0.0d;

		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			final Rectangle2D bounds;

			if (textAsShape) {
				final GlyphVector glyphV;

				{
					final char[] charBuff = new char[token.length()];
					token.getChars(0, charBuff.length, charBuff, 0);
					glyphV = font.layoutGlyphVector(grafx.getFontRenderContextFull(), charBuff, 0,
					                                charBuff.length, Font.LAYOUT_NO_LIMIT_CONTEXT);
				}

				bounds = glyphV.getLogicalBounds();
			} else
				bounds = font.getStringBounds(token, grafx.getFontRenderContextFull());

			width = Math.max(width, bounds.getWidth());
			height += bounds.getHeight();
		}

		rtrnVal2x[0] = (float) (width * fontScaleFactor);
		rtrnVal2x[1] = (float) (height * fontScaleFactor);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param grafx DOCUMENT ME!
	 * @param text DOCUMENT ME!
	 * @param font DOCUMENT ME!
	 * @param fontScaleFactor DOCUMENT ME!
	 * @param textXCenter DOCUMENT ME!
	 * @param textYCenter DOCUMENT ME!
	 * @param textJustify DOCUMENT ME!
	 * @param paint DOCUMENT ME!
	 * @param textAsShape DOCUMENT ME!
	 */
	public final static void renderHorizontalText(final GraphGraphics grafx, final String text,
	                                              final Font font, final double fontScaleFactor,
	                                              final float textXCenter, final float textYCenter,
	                                              final byte textJustify, final Paint paint,
	                                              final boolean textAsShape) {
		final float[] dims = new float[2];
		computeTextDimensions(grafx, text, font, fontScaleFactor, textAsShape, dims);

		final double overallWidth = dims[0];
		final double overallHeight = dims[1];
		double currHeight = overallHeight / -2.0d;
		final StringTokenizer tokenizer = new StringTokenizer(text, "\n");

		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken();
			final double textWidth;
			final double textHeight;

			if (textAsShape) {
				final GlyphVector glyphV;

				{
					final char[] charBuff = new char[token.length()];
					token.getChars(0, charBuff.length, charBuff, 0);
					glyphV = font.layoutGlyphVector(grafx.getFontRenderContextFull(), charBuff, 0,
					                                charBuff.length, Font.LAYOUT_NO_LIMIT_CONTEXT);
				}

				final Rectangle2D bounds = glyphV.getLogicalBounds();
				textWidth = fontScaleFactor * bounds.getWidth();
				textHeight = fontScaleFactor * bounds.getHeight();
			} else {
				final Rectangle2D bounds = font.getStringBounds(token,
				                                                grafx.getFontRenderContextFull());
				textWidth = fontScaleFactor * bounds.getWidth();
				textHeight = fontScaleFactor * bounds.getHeight();
			}

			final double yCenter = currHeight + textYCenter + (textHeight / 2.0d);
			final double xCenter;

			if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_CENTER)
				xCenter = textXCenter;
			else if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_LEFT)
				xCenter = (-0.5d * (overallWidth - textWidth)) + textXCenter;
			else if (textJustify == NodeDetails.LABEL_WRAP_JUSTIFY_RIGHT)
				xCenter = (0.5d * (overallWidth - textWidth)) + textXCenter;
			else
				throw new IllegalStateException("textJustify value unrecognized");

			grafx.drawTextFull(font, fontScaleFactor, token, (float) xCenter, (float) yCenter, 0,
			                   paint, textAsShape);
			currHeight += textHeight;
		}
	}
}

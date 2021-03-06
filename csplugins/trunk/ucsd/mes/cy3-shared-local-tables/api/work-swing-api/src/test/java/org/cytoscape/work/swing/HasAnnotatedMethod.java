/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.work.swing;


import org.cytoscape.work.Tunable;


public class HasAnnotatedMethod {
	@Tunable(description="An annotated field", groups={"group1"}, dependsOn="other=false",
		 params="input=true;alignments=horizontal;displayState=uncollapsed;groupTitles=hidden,displayed")
	public int getAnnotatedInt() { return value; }

	public void setAnnotatedInt(int x) { value = x; }

	@Tunable(description="Another annotated field", listenForChange={"AnnotatedInt"}) 
	public String getAnnotatedString() { return svalue; }

	public void setAnnotatedString(String x) { svalue = x; }

	private int value = 47; 

	private String svalue = "hello";
}

package cytoscape.render.test;

import cytoscape.geom.rtree.RTree;
import cytoscape.render.immed.GraphGraphics;
import cytoscape.util.intr.IntEnumerator;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public final class TestLODRendering
  extends Frame implements MouseListener, MouseMotionListener
{

  private static final double fudgeFactor = 10.0d;

  public final static void main(String[] args) throws Exception
  {
    final RTree tree;
    final float[] extents;
    final int N;
    final double sqrtN;

    // Populate the tree with entries.
    {
      N = Integer.parseInt(args[0]);
      tree = new RTree();
      extents = new float[N * 4]; // xMin1, yMin1, xMax1, yMax1, xMin2, ....
      sqrtN = Math.sqrt((double) N);
      int inx = 0;
      Random r = new Random();
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        double centerX =
          fudgeFactor * sqrtN * ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & r.nextInt();
        double centerY =
          fudgeFactor * sqrtN * ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & r.nextInt();
        double width =
          fudgeFactor * ((double) nonnegative) / ((double) 0x7fffffff);
        nonnegative = 0x7fffffff & r.nextInt();
        double height =
          fudgeFactor * ((double) nonnegative) / ((double) 0x7fffffff);
        extents[inx * 4] = (float) (centerX - (width / 2.0d));
        extents[(inx * 4) + 1] = (float) (centerY - (height / 2.0d));
        extents[(inx * 4) + 2] = (float) (centerX + (width / 2.0d));
        extents[(inx * 4) + 3] = (float) (centerY + (height / 2.0d));
        tree.insert(inx, extents[inx * 4], extents[(inx * 4) + 1],
                    extents[(inx * 4) + 2], extents[(inx * 4) + 3]);
        inx++; }
      for (inx = 0; inx < N; inx++) {
        // Re-insert every entry into tree for performance gain.
        tree.delete(inx);
        tree.insert(inx, extents[inx * 4], extents[(inx * 4) + 1],
                    extents[(inx * 4) + 2], extents[(inx * 4) + 3]); }
    }

    EventQueue.invokeAndWait(new Runnable() {
        public void run() {
          Frame f = new TestLODRendering(tree, extents, sqrtN);
          f.show();
          f.addWindowListener(new WindowAdapter() {
              public void windowClosing(WindowEvent e) {
                System.exit(0); } }); } });
  }

  private final RTree m_tree;
  private final float[] m_extents;
  private final float m_borderWidth = (float) (0.05d * fudgeFactor);
  private final int m_imgWidth = 600;
  private final int m_imgHeight = 600;
  private final Image m_img;
  private final GraphGraphics m_grafx;
  private final Color m_bgColor = Color.white;
  private final Color m_nodeColor = Color.black;
  private final Color m_borderColor = Color.red;
  private double m_currXCenter;
  private double m_currYCenter;
  private double m_currScale;
  private int m_currMouseButton = 0; // 0: none; 1: left; 2: middle.
  private int m_lastXMousePos = 0;
  private int m_lastYMousePos = 0;

  public TestLODRendering(RTree tree, float[] extents, double sqrtN)
  {
    super();
    m_tree = tree;
    m_extents = extents;
    m_currXCenter = fudgeFactor * sqrtN / 2.0d;
    m_currYCenter = fudgeFactor * sqrtN / 2.0d;
    m_currScale = fudgeFactor * ((double) m_imgWidth) / sqrtN;
    addNotify();
    m_img = createImage(m_imgWidth, m_imgHeight);
    m_grafx = new GraphGraphics(m_img, m_bgColor, true, false);
    updateNodeImage();
    addMouseListener(this);
    addMouseMotionListener(this);
  }

  public void paint(final Graphics g)
  {
    final Insets insets = insets();
    g.drawImage(m_img, insets.left, insets.top, null);
    resize(m_imgWidth + insets.left + insets.right,
           m_imgHeight + insets.top + insets.bottom);
  }

  public void update(final Graphics g)
  {
    final Insets insets = insets();
    updateNodeImage();
    g.drawImage(m_img, insets.left, insets.top, null);
  }

  private final void updateNodeImage()
  {
    m_grafx.clear(m_currXCenter, m_currYCenter, m_currScale);
    final IntEnumerator iter = m_tree.queryOverlap
      (m_currXCenter - ((double) (m_imgWidth / 2)) / m_currScale,
       m_currYCenter - ((double) (m_imgHeight / 2)) / m_currScale,
       m_currXCenter + ((double) (m_imgWidth / 2)) / m_currScale,
       m_currYCenter + ((double) (m_imgHeight / 2)) / m_currScale,
       null, 0);
    while (iter.numRemaining() > 0) {
      final int inx_x4 = iter.nextInt() * 4;
      m_grafx.drawNodeFull(GraphGraphics.SHAPE_ELLIPSE,
                           m_extents[inx_x4], m_extents[inx_x4 + 1],
                           m_extents[inx_x4 + 2], m_extents[inx_x4 + 3],
                           m_nodeColor, m_borderWidth, m_borderColor); }
  }

  public void mouseClicked(MouseEvent e) {}
  public void mouseEntered(MouseEvent e) {}
  public void mouseExited(MouseEvent e) {}

  public void mousePressed(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      m_currMouseButton = 1;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      m_currMouseButton = 2;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY(); }
  }

  public void mouseReleased(MouseEvent e)
  {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (m_currMouseButton == 1) m_currMouseButton = 0; }
    else if (e.getButton() == MouseEvent.BUTTON2) {
      if (m_currMouseButton == 2) m_currMouseButton = 0; }
  }

  public void mouseDragged(MouseEvent e)
  {
    if (m_currMouseButton == 1) {
      double deltaX = e.getX() - m_lastXMousePos;
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_currXCenter -= deltaX / m_currScale;
      m_currYCenter -= deltaY / m_currScale;
      repaint(); }
    else if (m_currMouseButton == 2) {
      double deltaY = e.getY() - m_lastYMousePos;
      m_lastXMousePos = e.getX();
      m_lastYMousePos = e.getY();
      m_currScale *= Math.pow(2, -deltaY / 300.0d);
      repaint(); }
  }

  public void mouseMoved(MouseEvent e) {}

  public boolean isResizable() { return false; }

}

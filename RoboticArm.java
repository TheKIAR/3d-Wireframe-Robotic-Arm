import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

public class RoboticArm extends JPanel {

    private double baseAngle = 0.4;
    private double shoulderAngle = -0.6;
    private double elbowAngle = 0.9;
    private double wristAngle = -0.3;

    private double targetBaseAngle = baseAngle;
    private double autoRotationSpeed = 0.012;
    private boolean autoSpinEnabled = true;
    private long lastInputTime = System.currentTimeMillis();
    private double targetShoulderAngle = shoulderAngle;
    private double targetElbowAngle = elbowAngle;
    private double targetWristAngle = wristAngle;

    private final double viewRotX = -0.35;
    private final double viewRotY = 0.6;

    private final double[] baseSize = {1.6, 1.6, 0.7};
    private final double[] upperArmSize = {0.52, 0.52, 2.2};
    private final double[] forearmSize = {0.42, 0.42, 1.8};
    private final double[] handSize = {0.62, 0.25, 0.6};
    private double clawOpen = 0.55;
    private double targetClawOpen = clawOpen;
    private double zoom = 1.0;

    private final int[][] boxEdges = {
        {0, 1}, {1, 2}, {2, 3}, {3, 0},
        {4, 5}, {5, 6}, {6, 7}, {7, 4},
        {0, 4}, {1, 5}, {2, 6}, {3, 7}
    };

    private final javax.swing.Timer animationTimer;

    public RoboticArm() {
        setPreferredSize(new Dimension(980, 720));
        setBackground(new Color(7, 10, 18));
        setFocusable(true);
        setDoubleBuffered(true);

        animationTimer = new javax.swing.Timer(16, e -> {
            long now = System.currentTimeMillis();
            if (!autoSpinEnabled && (now - lastInputTime) >= 5000) {
                autoSpinEnabled = true;
            }

            if (autoSpinEnabled) {
                baseAngle += autoRotationSpeed;
                if (baseAngle > Math.PI * 2) {
                    baseAngle -= Math.PI * 2;
                }
                if (baseAngle < -Math.PI * 2) {
                    baseAngle += Math.PI * 2;
                }
            }

            shoulderAngle += (targetShoulderAngle - shoulderAngle) * 0.12;
            elbowAngle += (targetElbowAngle - elbowAngle) * 0.12;
            wristAngle += (targetWristAngle - wristAngle) * 0.12;
            clawOpen += (targetClawOpen - clawOpen) * 0.18;
            repaint();
        });
        animationTimer.start();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                double step = 0.12;
                int key = e.getKeyCode();

                lastInputTime = System.currentTimeMillis();

                switch (key) {
                    case KeyEvent.VK_Q -> {
                        autoSpinEnabled = false;
                        targetBaseAngle = baseAngle;
                        baseAngle += step;
                    }
                    case KeyEvent.VK_A -> {
                        autoSpinEnabled = false;
                        targetBaseAngle = baseAngle;
                        baseAngle -= step;
                    }
                    case KeyEvent.VK_W -> {
                        autoSpinEnabled = false;
                        targetShoulderAngle += step;
                    }
                    case KeyEvent.VK_S -> {
                        autoSpinEnabled = false;
                        targetShoulderAngle -= step;
                    }
                    case KeyEvent.VK_E -> {
                        autoSpinEnabled = false;
                        targetElbowAngle += step;
                    }
                    case KeyEvent.VK_D -> {
                        autoSpinEnabled = false;
                        targetElbowAngle -= step;
                    }
                    case KeyEvent.VK_R -> {
                        autoSpinEnabled = false;
                        targetWristAngle += step;
                    }
                    case KeyEvent.VK_F -> {
                        autoSpinEnabled = false;
                        targetWristAngle -= step;
                    }
                    case KeyEvent.VK_Z -> {
                        autoSpinEnabled = false;
                        zoom = Math.max(0.9, zoom - 0.1);
                    }
                    case KeyEvent.VK_X -> {
                        autoSpinEnabled = false;
                        zoom = Math.min(2.6, zoom + 0.1);
                    }
                    case KeyEvent.VK_G -> {
                        autoSpinEnabled = false;
                        targetClawOpen = Math.min(1.2, targetClawOpen + 0.12);
                    }
                    case KeyEvent.VK_H -> {
                        autoSpinEnabled = false;
                        targetClawOpen = Math.max(0.15, targetClawOpen - 0.12);
                    }
                    case KeyEvent.VK_M -> autoSmartPose();
                    default -> {
                    }
                }

                keepWithinWorkspace();
                repaint();
            }
        });
    }

    private void autoSmartPose() {
        targetBaseAngle = 0.9;
        targetShoulderAngle = -1.0;
        targetElbowAngle = 1.2;
        targetWristAngle = -0.7;
        keepWithinWorkspace();
    }

    private void keepWithinWorkspace() {
        targetBaseAngle = clamp(targetBaseAngle, -1.15, 1.15);
        targetShoulderAngle = clamp(targetShoulderAngle, -1.25, 1.25);
        targetElbowAngle = clamp(targetElbowAngle, -1.65, 1.65);
        targetWristAngle = clamp(targetWristAngle, -1.15, 1.15);

        double shoulderPlusElbow = targetShoulderAngle + targetElbowAngle;
        if (shoulderPlusElbow > 1.5) {
            targetElbowAngle = 1.5 - targetShoulderAngle;
        } else if (shoulderPlusElbow < -1.5) {
            targetElbowAngle = -1.5 - targetShoulderAngle;
        }

        targetElbowAngle = clamp(targetElbowAngle, -1.65, 1.65);
        targetShoulderAngle = clamp(targetShoulderAngle, -1.25, 1.25);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double[][] rotationX(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[][]{{1, 0, 0}, {0, c, -s}, {0, s, c}};
    }

    private double[][] rotationY(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[][]{{c, 0, s}, {0, 1, 0}, {-s, 0, c}};
    }

    private double[][] rotationZ(double a) {
        double c = Math.cos(a), s = Math.sin(a);
        return new double[][]{{c, -s, 0}, {s, c, 0}, {0, 0, 1}};
    }

    private double[][] multiply(double[][] a, double[][] b) {
        double[][] r = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    r[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return r;
    }

    private double[] apply(double[][] m, double[] v) {
        return new double[]{
            m[0][0] * v[0] + m[0][1] * v[1] + m[0][2] * v[2],
            m[1][0] * v[0] + m[1][1] * v[1] + m[1][2] * v[2],
            m[2][0] * v[0] + m[2][1] * v[1] + m[2][2] * v[2]
        };
    }

    private double[] add(double[] a, double[] b) {
        return new double[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }

    private double[][] boxVertices(double width, double depth, double length) {
        double w = width / 2, d = depth / 2;
        return new double[][]{
            {-w, 0, -d}, {w, 0, -d}, {w, 0, d}, {-w, 0, d},
            {-w, length, -d}, {w, length, -d}, {w, length, d}, {-w, length, d}
        };
    }

    private Point project(double[] p, int width, int height) {
        double cameraDistance = 10;
        double scale = 260 * zoom;
        double factor = scale / (cameraDistance + p[2]);
        int x = (int) (p[0] * factor) + width / 2;
        int y = (int) (-p[1] * factor) + height / 2 + 110;
        return new Point(x, y);
    }

    private Color mix(Color base, double brightness) {
        int r = clamp((int) (base.getRed() * brightness));
        int g = clamp((int) (base.getGreen() * brightness));
        int b = clamp((int) (base.getBlue() * brightness));
        return new Color(r, g, b);
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private void drawSegment(Graphics2D g2, double[][] localVerts, double[][] worldRotation,
                             double[] pivot, int width, int height, Color baseColor) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));
        Point[] projected = new Point[localVerts.length];
        double[][] viewed = new double[localVerts.length][3];

        for (int i = 0; i < localVerts.length; i++) {
            double[] world = add(pivot, apply(worldRotation, localVerts[i]));
            viewed[i] = apply(view, world);
            projected[i] = project(viewed[i], width, height);
        }

        g2.setStroke(new BasicStroke(1.8f));
        for (int[] edge : boxEdges) {
            Point p1 = projected[edge[0]];
            Point p2 = projected[edge[1]];

            double t = (viewed[edge[0]][2] + viewed[edge[1]][2]) / 2.0;
            double glow = 0.55 + ((t + 8.0) / 16.0) * 0.9;
            glow = Math.max(0.45, Math.min(1.2, glow));

            g2.setColor(mix(baseColor, glow));
            g2.draw(new Line2D.Double(p1.x, p1.y, p2.x, p2.y));
        }
    }

    private void drawJointRing(Graphics2D g2, double[] pivot, double[][] rotation,
                              int width, int height, double radius, Color c) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));
        Point[] pts = new Point[8];

        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI / 4.0) * i;
            double[] local = {Math.cos(angle) * radius, Math.sin(angle) * radius, 0.0};
            double[] world = add(pivot, apply(rotation, local));
            Point p = project(apply(view, world), width, height);
            pts[i] = p;
        }

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < pts.length; i++) {
            int next = (i + 1) % pts.length;
            g2.drawLine(pts[i].x, pts[i].y, pts[next].x, pts[next].y);
        }
    }

    private void drawBaseAssembly(Graphics2D g2, double[][] baseRotation, int width, int height) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));

        double[] baseCenter = {0.0, 0.0, 0.0};
        double[] ringA = add(baseCenter, apply(baseRotation, new double[]{0.0, 0.1, 0.0}));
        double[] ringB = add(baseCenter, apply(baseRotation, new double[]{0.0, 0.45, 0.0}));
        double[] ringC = add(baseCenter, apply(baseRotation, new double[]{0.0, 0.75, 0.0}));

        drawDepthRing(g2, ringA, baseRotation, 1.1, 0.9, new Color(110, 220, 255), width, height, view);
        drawDepthRing(g2, ringB, baseRotation, 0.9, 0.7, new Color(90, 180, 255), width, height, view);
        drawDepthRing(g2, ringC, baseRotation, 0.7, 0.45, new Color(150, 200, 255), width, height, view);

        double[] p1 = add(baseCenter, apply(baseRotation, new double[]{0.85, 0.1, 0.85}));
        double[] p2 = add(baseCenter, apply(baseRotation, new double[]{-0.85, 0.1, 0.85}));
        double[] p3 = add(baseCenter, apply(baseRotation, new double[]{-0.85, 0.1, -0.85}));
        double[] p4 = add(baseCenter, apply(baseRotation, new double[]{0.85, 0.1, -0.85}));

        Point q1 = project(apply(view, p1), width, height);
        Point q2 = project(apply(view, p2), width, height);
        Point q3 = project(apply(view, p3), width, height);
        Point q4 = project(apply(view, p4), width, height);

        g2.setColor(new Color(110, 220, 255));
        g2.setStroke(new BasicStroke(1.8f));
        g2.drawLine(q1.x, q1.y, q2.x, q2.y);
        g2.drawLine(q2.x, q2.y, q3.x, q3.y);
        g2.drawLine(q3.x, q3.y, q4.x, q4.y);
        g2.drawLine(q4.x, q4.y, q1.x, q1.y);
    }

    private void drawDepthRing(Graphics2D g2, double[] pivot, double[][] rotation,
                              double outerRadius, double innerRadius,
                              Color c, int width, int height, double[][] view) {
        Point[] outer = new Point[12];
        Point[] inner = new Point[12];

        for (int i = 0; i < 12; i++) {
            double angle = (Math.PI / 6.0) * i;
            double[] o = apply(rotation, new double[]{Math.cos(angle) * outerRadius, 0.0, Math.sin(angle) * outerRadius});
            double[] iLocal = apply(rotation, new double[]{Math.cos(angle) * innerRadius, 0.0, Math.sin(angle) * innerRadius});
            outer[i] = project(apply(view, add(pivot, o)), width, height);
            inner[i] = project(apply(view, add(pivot, iLocal)), width, height);
        }

        g2.setColor(c);
        g2.setStroke(new BasicStroke(1.7f));
        for (int i = 0; i < 12; i++) {
            int next = (i + 1) % 12;
            g2.drawLine(outer[i].x, outer[i].y, outer[next].x, outer[next].y);
            g2.drawLine(inner[i].x, inner[i].y, inner[next].x, inner[next].y);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        GradientPaint background = new GradientPaint(0, 0, new Color(8, 12, 18), 0, height, new Color(14, 26, 38));
        g2.setPaint(background);
        g2.fillRect(0, 0, width, height);

        g2.setColor(new Color(42, 58, 76, 100));
        for (int x = 0; x < width; x += 40) {
            g2.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += 40) {
            g2.drawLine(0, y, width, y);
        }

        g2.setColor(new Color(90, 120, 160, 120));
        g2.fillOval(width / 2 - 52, height - 104, 104, 28);

        double[] origin = {0, 0, 0};
        double[][] baseRotation = rotationY(baseAngle);
        drawBaseAssembly(g2, baseRotation, width, height);
        double[] shoulderPivot = add(origin, apply(baseRotation, new double[]{0, baseSize[2], 0}));
        double[][] shoulderRotation = multiply(baseRotation, rotationZ(shoulderAngle));
        double[] elbowPivot = add(shoulderPivot, apply(shoulderRotation, new double[]{0, upperArmSize[2], 0}));
        double[][] elbowRotation = multiply(shoulderRotation, rotationZ(elbowAngle));
        double[] wristPivot = add(elbowPivot, apply(elbowRotation, new double[]{0, forearmSize[2], 0}));
        double[][] wristRotation = multiply(elbowRotation, rotationZ(wristAngle));

        g2.setColor(new Color(100, 220, 255, 150));
        g2.drawOval(width / 2 - 48, height - 100, 96, 24);

        drawJointRing(g2, shoulderPivot, shoulderRotation, width, height, 0.52, new Color(120, 255, 180));
        drawJointRing(g2, elbowPivot, elbowRotation, width, height, 0.42, new Color(255, 220, 120));
        drawJointRing(g2, wristPivot, wristRotation, width, height, 0.30, new Color(255, 180, 140));

        drawSegment(g2, boxVertices(baseSize[0], baseSize[1], baseSize[2]), baseRotation, origin, width, height, new Color(120, 220, 255));
        drawSegment(g2, boxVertices(upperArmSize[0], upperArmSize[1], upperArmSize[2]), shoulderRotation, shoulderPivot, width, height, new Color(120, 255, 180));
        drawSegment(g2, boxVertices(forearmSize[0], forearmSize[1], forearmSize[2]), elbowRotation, elbowPivot, width, height, new Color(255, 220, 120));

        drawTaperedForearm(g2, elbowPivot, wristPivot, elbowRotation, wristRotation, width, height, new Color(255, 220, 120));
        drawJointStruts(g2, shoulderPivot, elbowPivot, shoulderRotation, elbowRotation, width, height, new Color(120, 255, 180));
        drawJointStruts(g2, elbowPivot, wristPivot, elbowRotation, wristRotation, width, height, new Color(255, 220, 120));
        drawClaw(g2, wristRotation, wristPivot, width, height);

        g2.setColor(new Color(230, 245, 255));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.drawString("Q/A manual base   W/S shoulder   E/D elbow   R/F wrist   Z/X zoom   G/H claw   M smart pose", 24, height - 24);
    }

    private void drawClaw(Graphics2D g2, double[][] wristRotation, double[] pivot, int width, int height) {
        double fingerLength = 0.42;
        double palmWidth = 0.34;
        double palmDepth = 0.18;
        double leftSpread = -0.18 - clawOpen * 0.12;
        double rightSpread = 0.18 + clawOpen * 0.12;

        double[] base = add(pivot, apply(wristRotation, new double[]{0, handSize[2] * 0.2, -0.12}));
        drawLink(g2, pivot, base, wristRotation, width, height, new Color(255, 180, 140));

        double[][] palm = {
            {-palmWidth, 0.0, -palmDepth},
            {palmWidth, 0.0, -palmDepth},
            {palmWidth, 0.0, palmDepth},
            {-palmWidth, 0.0, palmDepth},
            {-palmWidth * 0.5, 0.18, -palmDepth * 0.5},
            {palmWidth * 0.5, 0.18, -palmDepth * 0.5},
            {palmWidth * 0.5, 0.18, palmDepth * 0.5},
            {-palmWidth * 0.5, 0.18, palmDepth * 0.5}
        };

        double[][] leftFinger = {
            {leftSpread, -0.02, -0.08},
            {leftSpread - 0.12, fingerLength * 0.66, -0.10},
            {leftSpread - 0.08, fingerLength + 0.10, 0.09},
            {leftSpread + 0.08, fingerLength * 0.82, 0.03}
        };

        double[][] middleFinger = {
            {0.0, -0.02, -0.08},
            {-0.04, fingerLength * 0.72, -0.06},
            {0.0, fingerLength + 0.12, 0.10},
            {0.04, fingerLength * 0.72, -0.06}
        };

        double[][] rightFinger = {
            {rightSpread, -0.02, -0.08},
            {rightSpread + 0.12, fingerLength * 0.66, -0.10},
            {rightSpread + 0.08, fingerLength + 0.10, 0.09},
            {rightSpread - 0.08, fingerLength * 0.82, 0.03}
        };

        double[][] thumb = {
            {0.0, -0.01, 0.14},
            {0.12, 0.18, 0.18 + clawOpen * 0.02},
            {0.06, 0.34, 0.12},
            {-0.05, 0.22, 0.10}
        };

        drawWireBox(g2, palm, wristRotation, base, width, height, new Color(255, 150, 120));
        drawFinger(g2, leftFinger, wristRotation, base, width, height, new Color(255, 150, 120));
        drawFinger(g2, middleFinger, wristRotation, base, width, height, new Color(255, 150, 120));
        drawFinger(g2, rightFinger, wristRotation, base, width, height, new Color(255, 150, 120));
        drawFinger(g2, thumb, wristRotation, base, width, height, new Color(255, 180, 140));
    }

    private void drawWireBox(Graphics2D g2, double[][] localVerts, double[][] rotation, double[] pivot,
                             int width, int height, Color c) {
        Point[] projected = new Point[localVerts.length];
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));
        for (int i = 0; i < localVerts.length; i++) {
            double[] world = add(pivot, apply(rotation, localVerts[i]));
            double[] viewed = apply(view, world);
            projected[i] = project(viewed, width, height);
        }

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.0f));
        int[][] edges = {
            {0, 1}, {1, 2}, {2, 3}, {3, 0},
            {4, 5}, {5, 6}, {6, 7}, {7, 4},
            {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };
        for (int[] edge : edges) {
            g2.drawLine(projected[edge[0]].x, projected[edge[0]].y, projected[edge[1]].x, projected[edge[1]].y);
        }
    }

    private void drawFinger(Graphics2D g2, double[][] points, double[][] wristRotation,
                            double[] pivot, int width, int height, Color c) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));
        Point[] projected = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            double[] world = add(pivot, apply(wristRotation, points[i]));
            double[] viewed = apply(view, world);
            projected[i] = project(viewed, width, height);
        }

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.0f));
        for (int i = 0; i < projected.length - 1; i++) {
            g2.drawLine(projected[i].x, projected[i].y, projected[i + 1].x, projected[i + 1].y);
        }
    }

    private void drawLink(Graphics2D g2, double[] a, double[] b, double[][] rotation,
                          int width, int height, Color c) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));
        double[] aWorld = add(a, apply(rotation, new double[]{0, 0, 0}));
        double[] bWorld = add(b, apply(rotation, new double[]{0, 0, 0}));
        Point p1 = project(apply(view, aWorld), width, height);
        Point p2 = project(apply(view, bWorld), width, height);

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(p1.x, p1.y, p2.x, p2.y);
    }

    private void drawJointStruts(Graphics2D g2, double[] aPivot, double[] bPivot,
                                double[][] aRotation, double[][] bRotation,
                                int width, int height, Color c) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));

        double[] p1 = add(aPivot, apply(aRotation, new double[]{-0.18, 0.0, -0.18}));
        double[] p2 = add(bPivot, apply(bRotation, new double[]{-0.18, 0.0, -0.18}));
        double[] p3 = add(aPivot, apply(aRotation, new double[]{0.18, 0.0, -0.18}));
        double[] p4 = add(bPivot, apply(bRotation, new double[]{0.18, 0.0, -0.18}));
        double[] p5 = add(aPivot, apply(aRotation, new double[]{-0.18, 0.0, 0.18}));
        double[] p6 = add(bPivot, apply(bRotation, new double[]{-0.18, 0.0, 0.18}));
        double[] p7 = add(aPivot, apply(aRotation, new double[]{0.18, 0.0, 0.18}));
        double[] p8 = add(bPivot, apply(bRotation, new double[]{0.18, 0.0, 0.18}));

        Point q1 = project(apply(view, p1), width, height);
        Point q2 = project(apply(view, p2), width, height);
        Point q3 = project(apply(view, p3), width, height);
        Point q4 = project(apply(view, p4), width, height);
        Point q5 = project(apply(view, p5), width, height);
        Point q6 = project(apply(view, p6), width, height);
        Point q7 = project(apply(view, p7), width, height);
        Point q8 = project(apply(view, p8), width, height);

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.2f));
        g2.drawLine(q1.x, q1.y, q2.x, q2.y);
        g2.drawLine(q3.x, q3.y, q4.x, q4.y);
        g2.drawLine(q5.x, q5.y, q6.x, q6.y);
        g2.drawLine(q7.x, q7.y, q8.x, q8.y);
    }

    private void drawTaperedForearm(Graphics2D g2, double[] aPivot, double[] bPivot,
                                   double[][] aRotation, double[][] bRotation,
                                   int width, int height, Color c) {
        double[][] view = multiply(rotationX(viewRotX), rotationY(viewRotY));

        double[] p1 = add(aPivot, apply(aRotation, new double[]{-0.16, 0.0, -0.12}));
        double[] p2 = add(bPivot, apply(bRotation, new double[]{-0.12, 0.0, -0.10}));
        double[] p3 = add(aPivot, apply(aRotation, new double[]{0.16, 0.0, -0.12}));
        double[] p4 = add(bPivot, apply(bRotation, new double[]{0.12, 0.0, -0.10}));
        double[] p5 = add(aPivot, apply(aRotation, new double[]{-0.16, 0.0, 0.12}));
        double[] p6 = add(bPivot, apply(bRotation, new double[]{-0.12, 0.0, 0.10}));
        double[] p7 = add(aPivot, apply(aRotation, new double[]{0.16, 0.0, 0.12}));
        double[] p8 = add(bPivot, apply(bRotation, new double[]{0.12, 0.0, 0.10}));

        Point q1 = project(apply(view, p1), width, height);
        Point q2 = project(apply(view, p2), width, height);
        Point q3 = project(apply(view, p3), width, height);
        Point q4 = project(apply(view, p4), width, height);
        Point q5 = project(apply(view, p5), width, height);
        Point q6 = project(apply(view, p6), width, height);
        Point q7 = project(apply(view, p7), width, height);
        Point q8 = project(apply(view, p8), width, height);

        g2.setColor(c);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawLine(q1.x, q1.y, q2.x, q2.y);
        g2.drawLine(q3.x, q3.y, q4.x, q4.y);
        g2.drawLine(q5.x, q5.y, q6.x, q6.y);
        g2.drawLine(q7.x, q7.y, q8.x, q8.y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RoboticArm panel = new RoboticArm();
            JFrame frame = new JFrame("Smart Robotic Arm");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }

}
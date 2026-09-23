# 3D Wireframe Robotic Arm

![Java CI](https://github.com/TheKIAR/3d-Wireframe-Robotic-Arm/actions/workflows/ci.yml/badge.svg)


A Java Swing project that renders an interactive robotic arm from scratch using 3D mathematics and a custom wireframe renderer.

## Features

- 3D robotic-arm model built from geometric primitives
- Rotation matrices for X, Y and Z axes
- Matrix multiplication and vector transformations
- Perspective projection from 3D coordinates to the 2D screen
- Hierarchical base, shoulder, elbow, wrist and claw movement
- Smooth joint interpolation
- Automatic base rotation with manual override
- Workspace and joint-angle limits
- Adjustable zoom
- Animated claw
- Smart pose
- Custom rendering with Java Swing/AWT — no external 3D engine

## Controls

| Key | Action |
|---|---|
| Q / A | Rotate base |
| W / S | Shoulder up / down |
| E / D | Elbow up / down |
| R / F | Wrist up / down |
| G / H | Open / close claw |
| Z / X | Zoom out / in |
| M | Smart pose |

After manual input, automatic base rotation resumes after a short idle period.

## How the 3D Rendering Works

The renderer follows a simple pipeline:

1. Define arm parts as local 3D vertices.
2. Apply joint rotation matrices.
3. Translate each part to its world-space pivot.
4. Apply the fixed camera/view rotation.
5. Project 3D coordinates onto the 2D Swing panel using perspective.
6. Connect projected vertices with line segments to create the wireframe.

For a rotation matrix R and vector v, the transformed vector is calculated as Rv.

## Project Structure

- App.java — application entry point and Swing window
- RoboticArm.java — robotic-arm model, transformations, animation and rendering

## Requirements

- Java 17 or newer
- Standard Java Swing/AWT libraries

## Run

Compile both Java files:

~~~bash
javac App.java RoboticArm.java
java App
~~~

Or open the project in IntelliJ IDEA, Eclipse, VS Code or another Java IDE and run App.

## Portfolio Focus

This project demonstrates:

- Java OOP
- 3D mathematics
- Linear algebra
- Matrix transformations
- Computer graphics fundamentals
- Event-driven programming
- Animation and interpolation
- Custom rendering

## Possible Future Improvements

- Mouse-controlled camera rotation
- Mouse-wheel zoom
- Camera reset
- Multiple projection modes
- Inverse kinematics
- Path planning for the end effector
- Export/import of robotic-arm poses


## 🌐 Links

**Portfolio:** https://ragibashhab.netlify.app/

**GitHub:** https://github.com/TheKIAR

**LinkedIn:** https://www.linkedin.com/in/md-ragib-ashhab-768a19240/

**Linktree:** https://linktr.ee/RagibAshhab

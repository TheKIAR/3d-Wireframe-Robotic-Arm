# 🦾 3D Wireframe Robotic Arm

<p align="center">
  <img src="assets/runtime-screenshot.png" alt="3D Wireframe Robotic Arm runtime" width="850">
</p>

<p align="center">
  <strong>Java • 3D Mathematics • Computer Graphics • Interactive Animation</strong><br>
  A robotic arm rendered from scratch with Java Swing/AWT and custom 3D transformations.
</p>

<p align="center">
  <a href="https://github.com/TheKIAR/3d-Wireframe-Robotic-Arm/actions/workflows/ci.yml"><img src="https://github.com/TheKIAR/3d-Wireframe-Robotic-Arm/actions/workflows/ci.yml/badge.svg" alt="Java CI"></a>
  <img src="https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk" alt="Java 17+">
  <img src="https://img.shields.io/badge/Graphics-3D-blue" alt="3D Graphics">
</p>

---

## 🎯 What this project does

This project turns linear algebra and computer-graphics theory into an interactive robotic arm you can control in real time.

No external 3D engine is used — the renderer, transformations and projection are implemented with standard Java and Swing/AWT.

## 🖼️ Runtime preview

<p align="center">
  <img src="assets/runtime-screenshot.png" alt="Robotic arm runtime screenshot" width="850">
</p>

<p align="center">
  <img src="assets/demo.gif" alt="Robotic arm runtime demo" width="850">
</p>

## ⚙️ Core features

- 🧱 3D robotic-arm model built from geometric primitives
- 🔄 X/Y/Z rotation matrices
- ✖️ Matrix multiplication and vector transformations
- 👁️ Perspective projection from 3D to 2D
- 🦾 Hierarchical base, shoulder, elbow, wrist and claw movement
- 🎞️ Smooth joint interpolation
- 🔁 Automatic base rotation with manual override
- 📐 Workspace and joint-angle limits
- 🔍 Adjustable zoom
- 🤏 Animated claw
- ✨ Smart pose

## 🎮 Controls

| Key | Action |
|---|---|
| **Q / A** | Rotate base |
| **W / S** | Shoulder up / down |
| **E / D** | Elbow up / down |
| **R / F** | Wrist up / down |
| **G / H** | Open / close claw |
| **Z / X** | Zoom out / in |
| **M** | Smart pose |

After manual input, automatic base rotation resumes after a short idle period.

## 🧮 Rendering pipeline

```text
Local 3D Geometry
       ↓
Joint Rotation Matrices
       ↓
World-Space Transformation
       ↓
Camera / View Rotation
       ↓
Perspective Projection
       ↓
2D Swing Canvas
```

For a rotation matrix **R** and vector **v**, the transformed vector is **Rv**.

## 🛠️ Run locally

**Requirements:** Java 17+.

```bash
javac App.java RoboticArm.java
java App
```

Or run `App` from IntelliJ IDEA, Eclipse, VS Code or another Java IDE.

## 📁 Project structure

```text
App.java          # Application entry point
RoboticArm.java   # Model, transformations, animation and rendering
```

## 🧠 What it demonstrates

**Programming:** Java OOP · event-driven programming · animation

**Mathematics:** linear algebra · matrices · vectors · transformations

**Graphics:** 3D coordinates · perspective projection · custom rendering

## 🚀 Future ideas

- Mouse-controlled camera
- Mouse-wheel zoom
- Camera reset
- Multiple projection modes
- Inverse kinematics
- End-effector path planning
- Pose export/import

## 👋 Connect

Built by **Md. Ragib Ashhab**.

🌐 [Portfolio](https://ragibashhab.netlify.app/) · 💼 [LinkedIn](https://www.linkedin.com/in/md-ragib-ashhab-768a19240/) · 🔗 [Linktree](https://linktr.ee/RagibAshhab) · 🐙 [GitHub](https://github.com/TheKIAR)

---

> **From matrices to motion — making computer graphics tangible.**

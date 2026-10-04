package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Flywheel PIDF Tuner")
public class FlywheelPIDFTuner extends OpMode {

    private DcMotorEx launcher;

    double P = 40;
    double I = 0;
    double D = 0;
    double F = 12.5;

    double targetVelocity = 1250;

    int selectedPIDF = 0;
    double step = 0.1;

    boolean lastUp = false;
    boolean lastDown = false;
    boolean lastLeft = false;
    boolean lastRight = false;
    boolean lastA = false;
    boolean lastB = false;

    @Override
    public void init() {

        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        setPIDF();
    }

    @Override
    public void loop() {

        // Right bumper = flywheel ON
        // Left bumper = flywheel OFF
        if (gamepad1.right_bumper) {
            launcher.setVelocity(targetVelocity);
        }

        if (gamepad1.left_bumper) {
            launcher.setVelocity(0);
        }

        // D-pad left/right = select PIDF value
        if (gamepad1.dpad_left && !lastLeft) {
            selectedPIDF--;
            if (selectedPIDF < 0) {
                selectedPIDF = 3;
            }
        }

        if (gamepad1.dpad_right && !lastRight) {
            selectedPIDF++;
            if (selectedPIDF > 3) {
                selectedPIDF = 0;
            }
        }

        // D-pad up/down = adjust selected value
        if (gamepad1.dpad_up && !lastUp) {
            adjustPIDF(step);
        }

        if (gamepad1.dpad_down && !lastDown) {
            adjustPIDF(-step);
        }

        // A/B = change adjustment step
        if (gamepad1.a && !lastA) {
            step /= 10.0;
            if (step < 0.0001) {
                step = 0.0001;
            }
        }

        if (gamepad1.b && !lastB) {
            step *= 10.0;
            if (step > 100) {
                step = 100;
            }
        }

        lastUp = gamepad1.dpad_up;
        lastDown = gamepad1.dpad_down;
        lastLeft = gamepad1.dpad_left;
        lastRight = gamepad1.dpad_right;
        lastA = gamepad1.a;
        lastB = gamepad1.b;

        telemetry.addData("Target Velocity", targetVelocity);
        telemetry.addData("Actual Velocity", launcher.getVelocity());

        telemetry.addData("P", P);
        telemetry.addData("I", I);
        telemetry.addData("D", D);
        telemetry.addData("F", F);

        telemetry.addData("Selected", getSelectedName());
        telemetry.addData("Step", step);

        telemetry.addData(
                "Error",
                targetVelocity - launcher.getVelocity()
        );

        telemetry.update();
    }

    private void adjustPIDF(double amount) {

        switch (selectedPIDF) {

            case 0:
                P += amount;
                if (P < 0) P = 0;
                break;

            case 1:
                I += amount;
                if (I < 0) I = 0;
                break;

            case 2:
                D += amount;
                if (D < 0) D = 0;
                break;

            case 3:
                F += amount;
                if (F < 0) F = 0;
                break;
        }

        setPIDF();
    }

    private void setPIDF() {

        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(P, I, D, F)
        );
    }

    private String getSelectedName() {

        switch (selectedPIDF) {

            case 0:
                return "P";

            case 1:
                return "I";

            case 2:
                return "D";

            case 3:
                return "F";

            default:
                return "";
        }
    }

    @Override
    public void stop() {
        launcher.setVelocity(0);
    }
}
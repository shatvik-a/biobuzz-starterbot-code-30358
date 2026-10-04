package org.firstinspires.ftc.teamcode;

import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Mec BioBuzz StarterBot Teleop", group = "StarterBot")
//@Disabled
public class BioBuzzLaunchingTeleop extends OpMode {

    private DcMotorEx launcher = null;
    private DcMotor intake = null;
    private CRServo rightIntakeServo = null;
    private CRServo windmillServo = null;

    public final int LAUNCHER_TARGET_VELOCITY = 1000;
    public final int LAUNCHER_MIN_VELOCITY = 1200;

    double intakePower;

    @Override
    public void init() {

        intake = hardwareMap.get(DcMotor.class, "intake");
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        windmillServo = hardwareMap.get(CRServo.class, "windmill");
        rightIntakeServo = hardwareMap.get(CRServo.class, "rightFeeder");

        intake.setZeroPowerBehavior(BRAKE);

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launcher.setPIDFCoefficients(
                DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(40, 0, 0, -12.5)
        );

        rightIntakeServo.setPower(0);
        windmillServo.setPower(0);


        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {

        // Right trigger = intake forward, left trigger = intake reverse
        intakePower = gamepad1.right_trigger - gamepad1.left_trigger;

        // Right bumper = launch
        launch();

        intake.setPower(intakePower);
        rightIntakeServo.setPower(intakePower);

        telemetry.addData(
                "Triggers",
                "left (%.2f, right (%.2f)",
                gamepad1.left_trigger,
                gamepad1.right_trigger
        );
        telemetry.addData("Launcher Power", launcher.getVelocity());
    }

    void launch() {

        // Hold right bumper to spin launcher
        if (gamepad1.right_bumper) {
            //launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
            launcher.setPower(0.5);
        } else {
            launcher.setVelocity(0);
        }

        // Feed only when launcher reaches minimum velocity
        if (gamepad1.right_bumper && launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
            windmillServo.setPower(1);
            intakePower += 0.5;
        } else {
            windmillServo.setPower(0);
        }
    }
}


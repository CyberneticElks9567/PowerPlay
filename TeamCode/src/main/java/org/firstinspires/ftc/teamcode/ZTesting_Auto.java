package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Hardware.Kd;
import static org.firstinspires.ftc.teamcode.Hardware.Ki;
import static org.firstinspires.ftc.teamcode.Hardware.Kp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Config
@Autonomous(name="ZTesting Auto", group="Auto")
public class ZTesting_Auto extends LinearOpMode {
    Hardware h = new Hardware();
    public static PIDController pidController = new PIDController(Kp,Ki,Kd,.25);
    FtcDashboard dashboard;

    public static double targetPos = 6000;
    double output = 0;

    //PIDController pid = new PIDController();
    OpenCvCamera webCam;
    @Override
    public void runOpMode() throws InterruptedException {
        try {
            h.init(hardwareMap, telemetry);
        } catch (Exception e) {
            telemetry.addData("Init Error:", "Something failed to initialize");
            e.printStackTrace();
        }

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.loggingEnabled      = false;

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        h.imu = hardwareMap.get(BNO055IMU.class, "imu");
        h.imu.initialize(parameters);
        telemetry.addData("Mode", "calibrating...");
        telemetry.update();
        int distanceEncodeVal = 385;

        dashboard = FtcDashboard.getInstance();

        TelemetryPacket packet = new TelemetryPacket();

        dashboard.setTelemetryTransmissionInterval(25);


        // make sure the imu gyro is calibrated before continuing.
        while (!isStopRequested() && !h.imu.isGyroCalibrated())
        {
            sleep(50);
            idle();
        }



        telemetry.addData("Mode", "waiting for start");
        telemetry.addData("imu calib status", h.imu.getCalibrationStatus().toString());
        telemetry.update();
        h.motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        h.motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);
        h.motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        h.motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);

        h.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        h.motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        h.motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        h.motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        h.motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();

        /*h.motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        h.motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
*/      while (!isStopRequested())
        {
            if(gamepad1.a)
            {
                break;
            }
        }

        while(!isStopRequested() && Math.abs(targetPos - h.motorFrontLeft.getCurrentPosition()) >= 10)
        {
            telemetry.addData("Targetpos: ", targetPos);
            telemetry.addData("MotorFrontLeft: ", h.motorFrontLeft.getCurrentPosition());
            telemetry.addData("MotorFrontRight: ", h.motorFrontRight.getCurrentPosition());
            telemetry.addData("MotorBackLeft: ", h.motorBackLeft.getCurrentPosition());
            telemetry.addData("MotorBackRight: ", h.motorBackRight.getCurrentPosition());
            telemetry.addData("Running...", "");
            packet.put("error", targetPos - h.motorFrontLeft.getCurrentPosition());
            packet.put("targetPos:  ", targetPos);
            packet.put("currentPos: ", h.motorFrontLeft.getCurrentPosition());
            telemetry.update();

            output = pidController.output(targetPos,h.motorFrontLeft.getCurrentPosition());
            h.motorFrontLeft.setPower(output);
            h.motorFrontRight.setPower(output);
            h.motorBackLeft.setPower(output);
            h.motorBackRight.setPower(output);
        }

        while(!isStopRequested())
        {
            telemetry.addData("Targetpos: ", targetPos);
            telemetry.addData("MotorFrontLeft: ", h.motorFrontLeft.getCurrentPosition());
            telemetry.addData("MotorFrontRight: ", h.motorFrontRight.getCurrentPosition());
            telemetry.addData("MotorBackLeft: ", h.motorBackLeft.getCurrentPosition());
            telemetry.addData("MotorBackRight: ", h.motorBackRight.getCurrentPosition());
            telemetry.addData("Finished", "");
            telemetry.update();

            h.motorFrontLeft.setPower(0);
            h.motorFrontRight.setPower(0);
            h.motorBackLeft.setPower(0);
            h.motorBackRight.setPower(0);

            h.sleep(1500);
        }

    }
}

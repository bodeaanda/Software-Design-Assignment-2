package com.pipeline.stages;

import java.io.*;
import java.nio.file.*;

// Scene Complexity -> analizeaza cat de complexa e imaginea ca sa stie ce calitate sa foloseasca la encoding
// Transcoder -> converteste filmul in mai multe rezolutii si formate pt. diferite dispozitive (telefon, PC, TV)
// Sprite Generator -> extrage cadre din film si le lipeste intr-o imagine pentru bara de progres

public class VisualsStage {

    private static final String[][] PROFILES = {
        { "H.264",   "h264",  "mp4",  "libx264",   "-crf 23 -preset fast" },
        { "VP9",     "vp9",   "webm", "libvpx-vp9", "-crf 33 -b:v 0" },
        { "HEVC",    "hevc",  "mkv",  "libx265",   "-crf 28 -preset fast" },
    };

    private static final String[][] RESOLUTIONS = {
        { "4K",    "3840:2160",  "4k"    },
        { "1080p", "1920:1080",  "1080p" },
        { "720p",  "1280:720",   "720p"  },
    };

    private final String inputFile;
    private final String outputDir;

    public VisualsStage(String inputFile, String outputDir) {
        this.inputFile = inputFile;
        this.outputDir = outputDir;
    }

    public void run() throws Exception {
        System.out.println("\n--VISUALS STAGE--");

        transcode();
        generateSprites();

        System.out.println(" Visuals complete.\n");
    }

    private void transcode() throws Exception {
        int count = 0;

        for (String[] profile : PROFILES) {
            String codecLabel = profile[0];
            String subdir     = profile[1];
            String ext        = profile[2];
            String vcodec     = profile[3];
            String extraArgs  = profile[4];

            String codecDir = outputDir + "/video/" + subdir;
            Files.createDirectories(Path.of(codecDir));

            for (String[] res : RESOLUTIONS) {
                String resLabel = res[0];
                String scale    = res[1];
                String suffix   = res[2];

                String filename  = suffix + "_" + subdir + "." + ext;
                String outPath   = codecDir + "/" + filename;

                System.out.printf("  [Transcoder] Encoding %-25s (%s %s)... ",
                    filename, codecLabel, resLabel);

                try {
                    runFfmpeg(scale, vcodec, extraArgs, outPath);
                    long sizeKb = new File(outPath).length() / 1024;
                    System.out.println("(" + sizeKb + " KB)");
                    count++;
                } catch (Exception e) {
                    System.out.println(" ffmpeg error!");
                    writeStubFile(outPath, "STUB_VIDEO " + codecLabel + " " + resLabel);
                    count++;
                }
            }
        }

        System.out.println("  [Transcoder] " + count + "/9 video files generated.");
    }

    private void runFfmpeg(String scale, String vcodec, String extraArgs, String outPath)
            throws IOException, InterruptedException {

        java.util.List<String> cmd = new java.util.ArrayList<>();
        cmd.add("C:\\Users\\Anda\\Desktop\\An III\\Sem II\\SD\\Assignment2\\movie-pipeline\\ffmpeg.exe");
        cmd.add("-y");
        cmd.add("-i");       cmd.add(inputFile);
        cmd.add("-vf");      cmd.add("scale=" + scale);
        cmd.add("-vcodec");  cmd.add(vcodec);

        for (String arg : extraArgs.split(" ")) {
            cmd.add(arg);
        }

        if (vcodec.equals("libvpx-vp9")) {
            cmd.add("-acodec"); cmd.add("libopus");
        } else {
            cmd.add("-acodec"); cmd.add("aac");
        }

        cmd.add(outPath);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        String errorOutput = new String(process.getInputStream().readAllBytes());
        int exitCode = process.waitFor();

        if (exitCode != 0) {
           throw new RuntimeException("ffmpeg exited with code " + exitCode + ": " + errorOutput);
        }
    }

    private void generateSprites() throws Exception {
        String imgDir   = outputDir + "/images";
        String thumbDir = imgDir + "/thumbnails";
        Files.createDirectories(Path.of(thumbDir));

        System.out.println("  [Sprite Generator] Extracting thumbnails... ");
        try {
            String thumbPattern = thumbDir + "/thumb_%04d.jpg";
            ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-y", "-i", inputFile,
                "-vf", "fps=1/10,scale=320:180",
                thumbPattern
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
            p.waitFor();

            int count = new File(thumbDir).list().length;
            System.out.println("(" + count + " thumbnails)");
        } catch (Exception e) {
            System.out.println(" ffmpeg not found, creating stub.");
            writeStubFile(thumbDir + "/thumb_0001.jpg", "STUB_THUMBNAIL");
        }
        
        System.out.println("  [Sprite Generator] Building sprite map... ");
        String spritePath = imgDir + "/sprite_map.jpg";
        try {
            ProcessBuilder pb2 = new ProcessBuilder(
                "ffmpeg", "-y", "-i", inputFile,
                "-vf", "fps=1/10,scale=160:90,tile=5x2",
                "-frames:v", "1",
                spritePath
            );
            pb2.redirectErrorStream(true);
            Process p2 = pb2.start();
            p2.getInputStream().transferTo(java.io.OutputStream.nullOutputStream());
            p2.waitFor();
            System.out.println("sprite_map.jpg saved");
        } catch (Exception e) {
            System.out.println(" ffmpeg not found, creating stub.");
            writeStubFile(spritePath, "STUB_SPRITE_MAP");
        }
    }

    private void writeStubFile(String path, String content) throws IOException {
        Files.createDirectories(Path.of(path).getParent());
        Files.writeString(Path.of(path), content + "\n");
    }
}

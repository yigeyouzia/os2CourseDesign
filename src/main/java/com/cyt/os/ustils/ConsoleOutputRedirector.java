package com.cyt.os.ustils;

import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextArea;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

/**
 * @author cyt
 * @date 2023/11/30 12:47
 */
public class ConsoleOutputRedirector {
    private static final BiFunction<TextArea, Long, ChangeListener<? super String>> lineClampListener = (t, l) -> (observable, oldValue, newValue) -> Platform.runLater(() -> {
        final String[] lines = newValue.split(StrUtil.LF);
        if (lines.length > l) {
            t.setText(
                    IntStream.range(lines.length - l.intValue(), lines.length).collect(
                            StringBuilder::new,
                            (s, i) -> s.append(lines[i]).append(StrUtil.LF),
                            (s1, s2) -> {
                                // ignore
                            }
                    ).toString()
            );
            t.setScrollTop(Double.MAX_VALUE);
        }
    });

    public static TextArea wrap(TextArea textArea) {
        // 默认不限制行数
        return wrap(textArea, Long.MAX_VALUE);
    }

    public static TextArea wrap(TextArea textArea, Long maxLine) {
        // 关掉旧输出流
        IOUtils.closeQuietly(System.out);
        // 控制行数的监听器
        textArea.textProperty().addListener(lineClampListener.apply(textArea, maxLine));
        // 新的输出流
        final PrintStream nextPrintStream = new PrintStream(new TextAreaOutputStream(textArea), true);
        // 重定向流
        System.setOut(nextPrintStream);
        System.setErr(System.out);
        return textArea;
    }

    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    private static class TextAreaOutputStream extends OutputStream {
        private final TextArea textArea;

        @Override
        public void write(int b) {
            Platform.runLater(() -> {
                textArea.appendText(String.valueOf((char) b));
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        }

        @Override
        public void write(byte[] b, int off, int len) {
            Platform.runLater(() -> {
                textArea.appendText(new String(b, off, len));
                textArea.setScrollTop(Double.MAX_VALUE);
            });
        }
    }
}


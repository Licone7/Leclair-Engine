package Leclair.window.win32;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.windows.MSG;
import org.lwjgl.system.windows.User32;
import org.lwjgl.system.windows.WNDCLASSEX;
import org.lwjgl.system.windows.WindowProc;
import org.lwjgl.system.windows.WindowsLibrary;

import Leclair.input.InputData;
import Leclair.window.WindowInfo;

public class testwindow {

    static long hwnd;
    static MSG msg = MSG.malloc();
    static WindowProc windowProc;

    public static void main(String[] args) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            windowProc = new WindowProc() {
                public long invoke(long hwnd, int uMsg, long wParam, long lParam) {
                    switch (uMsg) {
                        case User32.WM_CLOSE:
                            User32.DestroyWindow(hwnd);
                            System.exit(0);
                            // ApplicationStructure.stop();
                            break;
                        case User32.WM_KEYDOWN:
                            InputData.KEY_DOWN = true;
                            InputData.CHAR = (char) wParam;
                            break;
                    }
                    return User32.DefWindowProc(hwnd, uMsg, wParam, lParam);
                }
            };
            String className = "AWTAPPWNDCLASS";
            WNDCLASSEX in = WNDCLASSEX.calloc(stack);
            in.cbSize(WNDCLASSEX.SIZEOF);
            in.lpfnWndProc(windowProc);
            in.hInstance(WindowsLibrary.HINSTANCE);
            ByteBuffer classNameBuffer = stack.UTF16(className);
            in.lpszClassName(classNameBuffer);
            User32.RegisterClassEx(in);
            hwnd = User32.CreateWindowEx(User32.WS_EX_APPWINDOW, className, WindowInfo.getTitle(),
                    User32.WS_OVERLAPPEDWINDOW,
                    User32.CW_USEDEFAULT,
                    User32.CW_USEDEFAULT, WindowInfo.getWidth(), WindowInfo.getHeight(), 0, 0,
                    WindowsLibrary.HINSTANCE, windowProc.address());
            if (hwnd == 0) {
                throw new IllegalStateException("Cannot create a window");
            }
            User32.ShowWindow(hwnd, User32.SW_SHOW);
            loop();
        }
    }

    static void loop() {
        while (User32.PeekMessage(msg, hwnd, 0, 0, User32.PM_REMOVE)) {
            switch (msg.message()) {
                case User32.WM_KEYDOWN:
                    System.out.println("YAY!");
                    break;
                case User32.WM_PAINT:

                    break;
                case User32.WM_DESTROY:
                    User32.DestroyWindow(hwnd);
                    break;
                default:
                    break;
            }
           // System.out.println(msg.message());

        }
    }
}

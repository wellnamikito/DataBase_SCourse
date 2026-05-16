package backend.util;

import javax.swing.*;

/**
 * Защита от двойного сохранения (Enter + Button + listeners)
 */
public class SaveGuard {

    private boolean saving = false;

    /**
     * Безопасный вызов save-логики
     */
    public void run(Runnable saveAction) {
        if (saving) return;

        saving = true;
        try {
            saveAction.run();
        } finally {
            // важно: сброс должен быть в диалоге при ошибке
            // поэтому НЕ сбрасываем тут автоматически
        }
    }

    /**
     * Сброс блокировки (вызывать при ошибке)
     */
    public void reset() {
        saving = false;
    }

    /**
     * Биндим кнопку "Сохранить" безопасно
     */
    public void bindSaveButton(JButton button, Runnable saveAction) {

        button.addActionListener(e -> run(saveAction));

        // Enter = кнопка по умолчанию
        SwingUtilities.getWindowAncestor(button);
        button.getRootPane().setDefaultButton(button);
    }
}
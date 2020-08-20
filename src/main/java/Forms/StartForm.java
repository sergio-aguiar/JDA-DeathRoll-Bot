package Forms;

import Main.DeathRollMain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Forms: StartForm
 * <ul>
 *     <li> The application configuration window.
 *     <li> Conducts verification over input values.
 *     <li> Passes the input values to the main application.
 * </ul>
 *
 * @author Sérgio de Aguiar (pioavenger)
 * @version 1.1.1
 * @since 1.1.0
 */
public class StartForm extends JFrame
{
    /**
     * The form's base panel.
     */
    private JPanel mainPanel;
    /**
     * The form's text field relative to the discord bot's token.
     */
    private JTextField tokenTextField;
    /**
     * The form's label relative to the "discord bot's token" text field.
     */
    private JLabel tokenLabel;
    /**
     * The form's text field relative to the score value attributed to newly registering players.
     */
    private JTextField scoreTextField;
    /**
     * The form's label relative to the "score value attributed to newly registering" players text field.
     */
    private JLabel scoreLabel;
    /**
     * The form's run button.
     */
    private JButton RunButton;
    /**
     * Variable that states whether the application has been started or not.
     */
    private boolean started = false;
    /**
     * Class Constructor: StartForm.
     * @param title The form's window title.
     */
    public StartForm(String title)
    {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setContentPane(mainPanel);
        this.pack();

        tokenLabel.setFont(new Font(tokenLabel.getFont().getName(), Font.BOLD, 16));
        scoreLabel.setFont(tokenLabel.getFont());
        tokenTextField.setFont(new Font(tokenLabel.getFont().getName(), Font.PLAIN, 16));
        scoreTextField.setFont(tokenTextField.getFont());

        RunButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (!started && tokenTextField.getText() != null && !tokenTextField.getText().equals("")
                        && scoreTextField.getText() != null && !scoreTextField.getText().equals(""))
                {
                    int baseScore = -1;
                    try
                    {
                        baseScore = Integer.parseInt(scoreTextField.getText());
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }

                    if (baseScore > 0)
                    {
                        DeathRollMain.setToken(tokenTextField.getText());
                        DeathRollMain.setBaseScore(baseScore);
                        DeathRollMain.main(new String[0]);
                        started = true;
                        setVisible(false);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Invalid Score Value.");
                    }
                }
            }
        });
    }
    /**
     * The form's main function.
     * <ul>
     *     <li> Creates an instance of the application configuration window's form.
     *     <li> Adjusts the form's size and visibility.
     * </ul>
     * @param args Default main function's arguments (not used).
     */
    public static void main(String[] args)
    {
        JFrame frame = new StartForm("DeathRoll");
        frame.setSize(450,170);
        frame.setVisible(true);
    }
}

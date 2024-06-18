import java.util.Optional;

public class InOutManager {


    public InOutManager(Optional<InOutMethod> defaultIOMethod) {
        this._currentOutputMethod = defaultIOMethod.orElse(InOutMethod.Console);
    }



    public void SetIOMethod(InOutMethod outputMethod) {


    }

    public boolean Confirm(String confirmMessage) {
	    final String WORD_TO_CONFIRM = "Y";
	    final String WORD_TO_DISCARD = "N";
        String outputConfirmMessage = confirmMessage + "(" + WORD_TO_CONFIRM + "[yes] or " + WORD_TO_DISCARD + "[no]) ";
        if (_currentOutputMethod == InOutMethod.Console) {
            System.out.println(outputConfirmMessage);
        }
        String inputedString;
        boolean confirmed;
        while (true) {
            inputedString = this.GetString();
            if (inputedString.equals(WORD_TO_CONFIRM)) {
                confirmed = true;
                break;
            } else if (inputedString.equals(WORD_TO_DISCARD)) {
                confirmed = false;
                break;
            }
        }
        return confirmed;
    }

    public void OutputString(String outputMessage) {


    }
    public void ClearOutput() {


    }

    public String GetString() {

        return "";
    }


    private InOutMethod _currentOutputMethod;

}

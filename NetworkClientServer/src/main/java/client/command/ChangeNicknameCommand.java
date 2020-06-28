package client.command;

import java.io.Serializable;

public class ChangeNicknameCommand implements Serializable {

    private  final String newNickname;

    private String nickname;

    public ChangeNicknameCommand(String newNickname) {
        this.newNickname = newNickname;
    }


    public String getNewNickname() {
        return newNickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /*public void setOldNickname(String oldNickname) {
        this.oldNickname = oldNickname;
    }

    public void setNewNickname(String newNickname) {
        this.newNickname = newNickname;
    }*/
}

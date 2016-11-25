package my.assignment.todoproject.Model;

/**
 * Created by root on 9/1/16.
 */

public class Item {
    String itemName;
    boolean check;

    public Item(String name,boolean b){
        itemName=name;
        check=b;

    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}

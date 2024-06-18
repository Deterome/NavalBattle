package NavalBattleGame.GameElements;

public class Sea {

    public Sea(int seaWidth, int seaHeight) {
        this.seaTable = new SeaCellInfo[seaWidth][seaHeight];
    }

    private SeaCellInfo[][] seaTable;

    public void AttackCell(int x, int y) {

        if (x >= this.seaTable.length || x < 0) {
            throw new IndexOutOfBoundsException("Trying to access non-existent sea cell (x out of bounds)!");
        }
        if (y >= this.seaTable[0].length || y < 0) {
            throw new IndexOutOfBoundsException("Trying to access non-existent sea cell (y out of bounds)!");
        }

        var cellInfo =  this.seaTable[x][y];

        if (cellInfo.ship != null) {
            cellInfo.ship.GetDamageAtPart(cellInfo.shipPartId);
        }

    }


}

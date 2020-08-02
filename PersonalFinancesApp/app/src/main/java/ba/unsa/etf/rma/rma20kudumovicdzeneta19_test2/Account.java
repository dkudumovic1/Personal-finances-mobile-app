package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

public class Account {
    private Integer id;
    private double budget;
    private double totalLimit;
    private double monthLimit;
    private Integer internalId;
    //todo vidi za internalId da li je nepotrebno

    public Account() { }

    public Account(Integer id, double budget, double totalLimit, double monthLimit, Integer internalId) {
        this.id = id;
        this.budget = budget;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
        this.internalId = internalId;
    }

    public Account(Integer id, double budget, double totalLimit, double monthLimit) {
        this.id = id;
        this.budget = budget;
        this.totalLimit = totalLimit;
        this.monthLimit = monthLimit;
    }

    public Integer getInternalId() {
        return internalId;
    }

    public void setInternalId(Integer internalId) {
        this.internalId = internalId;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getTotalLimit() {
        return totalLimit;
    }

    public void setTotalLimit(double totalLimit) {
        this.totalLimit = totalLimit;
    }

    public double getMonthLimit() {
        return monthLimit;
    }

    public void setMonthLimit(double monthLimit) {
        this.monthLimit = monthLimit;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

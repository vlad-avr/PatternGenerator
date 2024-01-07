package pattern.builders;

interface HouseBuilderInterface{
    public void reset();
	public void setNumberOfRooms(int val);
	public void setSquareMeters(double val);
	public void setEbal(boolean val);
	public void setDateOfFounding(java.time.LocalDate val);
	public void setInhabitants(java.util.List<com.example.FactoryTest.Human> val);

}


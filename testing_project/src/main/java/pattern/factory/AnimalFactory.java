package pattern.factory;

import com.example.FactoryTest.Animal;
import com.example.FactoryTest.Bird;
import com.example.FactoryTest.Dog;
import com.example.FactoryTest.Cat;

class AnimalFactory{

    public static enum Option{
		FIREBIRD,
		WHO_LET_THE_DOGS_OUT,
		PUSSY,

    }

    public static Animal makeAnimal(Option option){
        switch(option){
			case FIREBIRD:
				return new Bird();
			case WHO_LET_THE_DOGS_OUT:
				return new Dog();
			case PUSSY:
				return new Cat();

            default:
                return null;
        }
    }
}


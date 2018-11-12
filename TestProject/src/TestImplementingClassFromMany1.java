
public class TestImplementingClassFromMany1 implements TestInterfaceWithManyChildren {

	@Override
	public int return_one() {
		return 1;
	}

	@Override
	public int return_the_input(int input) {
		return input;
	}

	@Override
	public int return_the_input_times_ten(int input) {
		return input*10;
	}

}

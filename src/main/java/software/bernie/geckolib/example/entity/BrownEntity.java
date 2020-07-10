package software.bernie.geckolib.example.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animation.AnimationBuilder;
import software.bernie.geckolib.animation.AnimationTestEvent;
import software.bernie.geckolib.animation.model.AnimationController;
import software.bernie.geckolib.animation.model.AnimationControllerCollection;
import software.bernie.geckolib.entity.IAnimatedEntity;

import javax.annotation.Nullable;

public class BrownEntity extends AnimalEntity implements IAnimatedEntity
{
	AnimationControllerCollection collection = new AnimationControllerCollection();
	AnimationController controller = new AnimationController(this, "controller", 30, this::predicate);

	private <ENTITY extends Entity> boolean predicate(AnimationTestEvent<ENTITY> event)
	{
		controller.setAnimation(new AnimationBuilder().addAnimation("running", true));
		return true;
	}

	public BrownEntity(EntityType<? extends AnimalEntity> type, World worldIn)
	{
		super(type, worldIn);
		collection.addAnimationController(controller);
	}

	@Nullable
	@Override
	public AgeableEntity createChild(AgeableEntity ageable)
	{
		return null;
	}

	@Override
	public AnimationControllerCollection getAnimationControllers()
	{
		return collection;
	}
}

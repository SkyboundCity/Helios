package city.skybound.helios.soul;

import city.skybound.helios.config.HoconConfigurateWrapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import dev.tehbrian.agna.configurate.AbstractDataConfig;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Stores persistent data for souls via a YAML config.
 * <p>
 * <a href="https://en.wikipedia.org/wiki/Guf">If you're curious about the name.</a>
 */
public final class Otzar extends AbstractDataConfig<HoconConfigurateWrapper, Otzar.Data> {

	@Inject
	public Otzar(final @Named("dataFolder") Path dataFolder) {
		super(new HoconConfigurateWrapper(
				dataFolder.resolve("otzar.conf"), HoconConfigurationLoader.builder()
				.path(dataFolder.resolve("otzar.conf"))
				.build()
		));
	}

	public void save() throws ConfigurateException {
		final CommentedConfigurationNode rootNode = Objects.requireNonNull(this.wrapper().rootNode());
		rootNode.set(this.dataClass(), this.data);
		this.wrapper().save();
	}

	public Map<UUID, Data.Spirit> spirits() {
		return this.data().spirits();
	}

	@Override
	protected Class<Otzar.Data> dataClass() {
		return Otzar.Data.class;
	}

	@ConfigSerializable
	public record Data(Map<UUID, Spirit> spirits) {

		@ConfigSerializable
		public record Spirit(Integer netherInfractions,
		                     Boolean markdown) {

		}

	}

}

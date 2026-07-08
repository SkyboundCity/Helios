package city.skybound.helios.inject;

import city.skybound.helios.LuckPermsService;
import city.skybound.helios.config.BooksConfig;
import city.skybound.helios.config.ConfigConfig;
import city.skybound.helios.config.EmotesConfig;
import city.skybound.helios.config.LangConfig;
import city.skybound.helios.config.PianoNotesConfig;
import city.skybound.helios.nextbot.Nate;
import city.skybound.helios.realm.Transposer;
import city.skybound.helios.realm.WorldService;
import city.skybound.helios.soul.Charon;
import city.skybound.helios.soul.Otzar;
import city.skybound.helios.tag.TagGame;
import city.skybound.helios.transportation.FlightService;
import city.skybound.helios.transportation.PortalListener;
import com.google.inject.AbstractModule;

public final class SingletonModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(FlightService.class).asEagerSingleton();
		this.bind(LuckPermsService.class).asEagerSingleton();
		this.bind(TagGame.class).asEagerSingleton();
		this.bind(Charon.class).asEagerSingleton();
		this.bind(WorldService.class).asEagerSingleton();
		this.bind(Transposer.class).asEagerSingleton();
		this.bind(Nate.class).asEagerSingleton();
		this.bind(PortalListener.class).asEagerSingleton();
		this.bind(BooksConfig.class).asEagerSingleton();
		this.bind(ConfigConfig.class).asEagerSingleton();
		this.bind(EmotesConfig.class).asEagerSingleton();
		this.bind(PianoNotesConfig.class).asEagerSingleton();
		this.bind(LangConfig.class).asEagerSingleton();
		this.bind(Otzar.class).asEagerSingleton();
	}

}

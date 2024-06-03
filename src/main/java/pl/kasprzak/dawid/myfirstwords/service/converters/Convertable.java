package pl.kasprzak.dawid.myfirstwords.service.converters;

public interface Convertable<DtoInputType, Entity, DtoOutputType> {

    Entity fromDto(DtoInputType input);

    DtoOutputType toDto(Entity entity);
}

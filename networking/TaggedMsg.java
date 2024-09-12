package sysc3303_elevator.networking;

public record TaggedMsg<I, T>(I id, T content) {

    public <E> TaggedMsg<I, E> replaceContent(E content) {
        return new TaggedMsg<>(id, content);
    }
}

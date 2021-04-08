package com.atoledano.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.EntitySystem;
import com.artemis.utils.Bag;
import com.artemis.utils.Sort;
import com.atoledano.components.Renderer;
import com.atoledano.components.Transform;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RenderSystem extends EntitySystem {

    private final SpriteBatch batch;

    protected ComponentMapper<Transform> transformComponentMapper;
    protected ComponentMapper<Renderer> rendererComponentMapper;

    public RenderSystem(SpriteBatch batch) {
        super(Aspect.all(Transform.class, Renderer.class));

        this.batch = batch;
    }

    @Override
    protected void begin() {
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    protected void process(Entity e) {
        Transform transform = transformComponentMapper.get(e);
        Renderer renderer = rendererComponentMapper.get(e);

        renderer.setPosition(transform.posX, transform.posY);
        renderer.setRotation(transform.rotation);
        renderer.setScale(transform.sclX, transform.sclY);

        renderer.draw(batch);
    }

    @Override
    protected void processSystem() {
        Bag<Entity> entities = getEntities();

        Sort sort = Sort.instance();
        sort.sort(entities, (o1, o2) -> {
            Transform t1 = transformComponentMapper.get(o1);
            Transform t2 = transformComponentMapper.get(o2);
            return Float.compare(t2.z, t1.z);
        });

        for (Entity e : entities) {
            process(e);
        }
    }

}

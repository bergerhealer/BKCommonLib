package com.bergerkiller.bukkit.common.utils;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import com.bergerkiller.bukkit.common.utils.EntityGroupingUtil;
import com.bergerkiller.bukkit.common.utils.EntityGroupingUtil.EntityCategory;

import org.bukkit.entity.*;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class EntityGroupingUtilTest {

    public static Entity generateEntity(EntityType entityType) {
        return generateEntity(entityType.getEntityClass());
    }

    public static Entity generateEntity(Class<?> entityClass) {
        return (Entity) mock(entityClass);
    }

    @Test
    public void testGetCategoryString() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                @SuppressWarnings("deprecation")
                EntityCategory result1 = EntityGroupingUtil.getCategory(test.getMatching().getName().toLowerCase());
                assertSame(String.format("%s: (%s) == (%s)", test.name(), test.getEntityCategory().name(), result1.toString()), result1, test.getEntityCategory());

                @SuppressWarnings("deprecation")
                EntityCategory result2 = EntityGroupingUtil.getCategory(test.getNonMatching().getName().toLowerCase());
                assertNotSame(String.format("%s: (%s) != (%s)", test.name(), test.getEntityCategory().name(), result2.toString()), result2, test.getEntityCategory());
            }
        }
    }

    @Test
    public void testGetCategoriesString() {
        for (EntityTestList test : EntityTestList.values()) {
            @SuppressWarnings("deprecation")
            Set<EntityCategory> result1 = EntityGroupingUtil.getCategories(test.getMatching().getName().toLowerCase());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getEntityCategory().name(), result1.toString()), result1.contains(test.getEntityCategory()));

            @SuppressWarnings("deprecation")
            Set<EntityCategory> result2 = EntityGroupingUtil.getCategories(test.getNonMatching().getName().toLowerCase());
            assertTrue(String.format("%s: (%s) != (%s)", test.name(), test.getEntityCategory().name(), result2.toString()), result1.contains(test.getEntityCategory()));
        }
    }

    @Test
    public void testGetCategoryEntityType() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                @SuppressWarnings("deprecation")
                EntityCategory result1 = EntityGroupingUtil.getCategory(test.getMatching());
                assertSame("Matching " + test.name(), result1, test.getEntityCategory());

                @SuppressWarnings("deprecation")
                EntityCategory result2 = EntityGroupingUtil.getCategory(test.getNonMatching());
                assertNotSame("Non-Matching " + test.name(), result2, test.getEntityCategory());
            }
        }
    }

    @Test
    public void testGetCategoriesEntityType() {
        for (EntityTestList test : EntityTestList.values()) {
            Set<EntityCategory> result1 = EntityGroupingUtil.getCategories(test.getMatching());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getEntityCategory().name(), result1.toString()), result1.contains(test.getEntityCategory()));

            Set<EntityCategory> result2 = EntityGroupingUtil.getCategories(test.getNonMatching());
            assertTrue(String.format("%s: (%s) != (%s)", test.name(), test.getEntityCategory().name(), result2.toString()), result1.contains(test.getEntityCategory()));
        }
    }

    @Test
    public void testGetCategoryClassOfQextendsEntity() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                @SuppressWarnings("deprecation")
                EntityCategory result1 = EntityGroupingUtil.getCategory(test.getMatching().getEntityClass());
                assertSame("Matching " + test.name(), result1, test.getEntityCategory());

                @SuppressWarnings("deprecation")
                EntityCategory result2 = EntityGroupingUtil.getCategory(test.getNonMatching().getEntityClass());
                assertNotSame("Non-Matching " + test.name(), result2, test.getEntityCategory());
            }
        }
    }

    @Test
    public void testGetCategoriesEntity() {
        for (EntityTestList test : EntityTestList.values()) {
            Entity entity1 = generateEntity(test.getMatching());
            Set<EntityCategory> result1 = EntityGroupingUtil.getCategories(entity1);
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getEntityCategory().name(), result1.toString()), result1.contains(test.getEntityCategory()));

            Entity entity2 = generateEntity(test.getNonMatching());
            Set<EntityCategory> result2 = EntityGroupingUtil.getCategories(entity2);
            assertTrue(String.format("%s: (%s) != (%s)", test.name(), test.getEntityCategory().name(), result2.toString()), result1.contains(test.getEntityCategory()));
        }
    }

    @Test
    public void testGetCategoriesClassOfQextendsEntity() {
        for (EntityTestList test : EntityTestList.values()) {
            Set<EntityCategory> result1 = EntityGroupingUtil.getCategories(test.getMatching().getEntityClass());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getEntityCategory().name(), result1.toString()), result1.contains(test.getEntityCategory()));

            Set<EntityCategory> result2 = EntityGroupingUtil.getCategories(test.getNonMatching().getEntityClass());
            assertTrue(String.format("%s: (%s) != (%s)", test.name(), test.getEntityCategory().name(), result2.toString()), result1.contains(test.getEntityCategory()));
        }
    }

    @Test
    public void testIsEntityTypeStringEntityCategory() {
        for (EntityTestList test : EntityTestList.values()) {
            @SuppressWarnings("deprecation")
            boolean result1 = EntityGroupingUtil.isEntityType(test.getMatching().getName(), test.getEntityCategory());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory().name()), result1);

            @SuppressWarnings("deprecation")
            boolean result2 = EntityGroupingUtil.isEntityType(test.getNonMatching().getName(), test.getEntityCategory());
            assertFalse(String.format("%s: (%s) != (%s)", test.name(), test.getNonMatching().name(), test.getEntityCategory().name()), result2);
       }
    }

    @Test
    public void testIsEntityTypeClassEntityClassOfQ() {
        for (EntityTestList test : EntityTestList.values()) {
            Entity entity1 = generateEntity(test.getMatching());
            boolean result1 = EntityGroupingUtil.isEntityTypeClass(entity1, test.getMatching().getEntityClass());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory().name()), result1);

            Entity entity2 = generateEntity(test.getNonMatching());
            boolean result2 = EntityGroupingUtil.isEntityTypeClass(entity2, test.getMatching().getEntityClass());
            assertFalse(String.format("%s: (%s) != (%s)", test.name(), test.getNonMatching().name(), test.getEntityCategory().name()), result2);
       }
    }

    @Test
    public void testIsEntityTypeClassEntitySetOfClassOfQ() {
        for (EntityTestList test : EntityTestList.values()) {
            Entity entity1 = generateEntity(test.getMatching());
            boolean result1 = EntityGroupingUtil.isEntityTypeClass(entity1, test.getEntityCategory().getEntityClasses());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory().getEntityClasses().toString()), result1);

            Entity entity2 = generateEntity(test.getNonMatching());
            boolean result2 = EntityGroupingUtil.isEntityTypeClass(entity2, test.getEntityCategory().getEntityClasses());
            assertFalse(String.format("%s: (%s) != (%s)", test.name(), test.getNonMatching().name(), test.getEntityCategory().getEntityClasses().toString()), result2);
       }
    }

    @Test
    public void testIsEntityTypeEntityEntityCategory() {
        for (EntityTestList test : EntityTestList.values()) {
            Entity entity1 = generateEntity(test.getMatching());
            boolean result1 = EntityGroupingUtil.isEntityType(entity1, test.getEntityCategory());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory().toString()), result1);

            Entity entity2 = generateEntity(test.getNonMatching());
            boolean result2 = EntityGroupingUtil.isEntityType(entity2, test.getEntityCategory());
            assertFalse(String.format("%s: (%s) != (%s)", test.name(), test.getNonMatching().name(), test.getEntityCategory().toString()), result2);
       }
    }

    @Test
    public void testIsEntityTypeEntitySetOfEntityCategory() {
        for (EntityTestList test : EntityTestList.values()) {
            Entity entity1 = generateEntity(test.getMatching());
            Set<EntityCategory> set1 = new HashSet<EntityCategory>();
            set1.add(test.getEntityCategory());
            boolean result1 = EntityGroupingUtil.isEntityType(entity1, set1);
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), set1.toString()), result1);

            Entity entity2 = generateEntity(test.getNonMatching());
            Set<EntityCategory> set2 = new HashSet<EntityCategory>();
            set2.add(test.getEntityCategory());
            boolean result2 = EntityGroupingUtil.isEntityType(entity2, set2);
            assertFalse(String.format("%s: (%s) != (%s)", test.name(), test.getNonMatching().name(), set2.toString()), result2);
       }
    }

    @Test
    public void testIsEntityTypeEntityTypeEntityCategory() {
        for (EntityTestList test : EntityTestList.values()) {
            boolean result1 = EntityGroupingUtil.isEntityType(test.getMatching(), test.getEntityCategory());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result1);

            boolean result2 = EntityGroupingUtil.isEntityType(test.getNonMatching(), test.getEntityCategory());
            assertFalse(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result2);
       }
    }

    @Test
    public void testIsEntityTypeClassClassOfQClassOfQ() {
        for (EntityTestList test : EntityTestList.values()) {
            boolean result1 = EntityGroupingUtil.isEntityTypeClass(test.getMatching().getEntityClass(), test.getMatching().getEntityClass());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result1);

            boolean result2 = EntityGroupingUtil.isEntityTypeClass(test.getNonMatching().getEntityClass(), test.getMatching().getEntityClass());
            assertFalse(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result2);
       }
    }

    @Test
    public void testIsEntityTypeClassClassOfQEntityCategory() {
        for (EntityTestList test : EntityTestList.values()) {
            boolean result1 = EntityGroupingUtil.isEntityTypeClass(test.getMatching().getEntityClass(), test.getEntityCategory());
            assertTrue(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result1);

            boolean result2 = EntityGroupingUtil.isEntityTypeClass(test.getNonMatching().getEntityClass(), test.getEntityCategory());
            assertFalse(String.format("%s: (%s) == (%s)", test.name(), test.getMatching().name(), test.getEntityCategory()), result2);
       }
    }

    @Test
    public void testIsMobString() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                boolean result1 = EntityGroupingUtil.isMob(test.getMatching().name());
                assertTrue(String.format("%s: isMob(%s)", test.name(), test.getMatching().name()), result1);
            }
        }
        boolean result2 = EntityGroupingUtil.isMob("minecart");
        assertFalse(String.format("isMob(%s)", "minecart"), result2);
    }

    @Test
    public void testIsMobEntity() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                Entity entity = generateEntity(test.getMatching().getEntityClass());
                boolean result1 = EntityGroupingUtil.isMob(entity);
                assertTrue(String.format("%s: isMob(%s)", test.name(), test.getMatching().name()), result1);
            }
        }
        Entity entity = generateEntity(Minecart.class);
        boolean result2 = EntityGroupingUtil.isMob(entity);
        assertFalse(String.format("isMob(%s)", "minecart entity"), result2);
    }

    @Test
    public void testIsMobEntityType() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                boolean result1 = EntityGroupingUtil.isMob(test.getMatching());
                assertTrue(String.format("%s: isMob(%s entity type)", test.name(), test.getMatching().name()), result1);
            }
        }
        boolean result2 = EntityGroupingUtil.isMob(EntityType.MINECART);
        assertFalse(String.format("isMob(%s)", "minecart entity type"), result2);
    }

    @Test
    public void testIsMobClassOfQextendsEntity() {
        for (EntityTestList test : EntityTestList.values()) {
            if (test.isLegacyField()) {
                boolean result1 = EntityGroupingUtil.isMob(test.getMatching().getEntityClass());
                assertTrue(String.format("%s: isMob(%s class)", test.name(), test.getMatching().name()), result1);
            }
        }
        boolean result2 = EntityGroupingUtil.isMob(Minecart.class);
        assertFalse(String.format("isMob(%s)", "minecart class"), result2);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsNPCString() {
        assertTrue("Villager is NPC", EntityGroupingUtil.isNPC("villager"));
        assertFalse("Zombie is not NPC", EntityGroupingUtil.isNPC("zombie"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsNPCEntity() {
        Entity villager = generateEntity(Villager.class);
        Entity zombie = generateEntity(Zombie.class);
        assertTrue("Villager is NPC", EntityGroupingUtil.isNPC(villager));
        assertFalse("Zombie is not NPC", EntityGroupingUtil.isNPC(zombie));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsNPCEntityType() {
        assertTrue("Villager is NPC", EntityGroupingUtil.isNPC(EntityType.VILLAGER));
        assertFalse("Zombie is not NPC", EntityGroupingUtil.isNPC(EntityType.ZOMBIE));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsNPCClassOfQextendsEntity() {
        assertTrue("Villager is NPC", EntityGroupingUtil.isNPC(Villager.class));
        assertFalse("Zombie is not NPC", EntityGroupingUtil.isNPC(Zombie.class));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsAnimalString() {
        assertTrue("Sheep is Animal", EntityGroupingUtil.isAnimal("sheep"));
        assertFalse("Zombie is not Animal", EntityGroupingUtil.isAnimal("zombie"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsAnimalEntity() {
        Entity sheep = generateEntity(Sheep.class);
        Entity zombie = generateEntity(Zombie.class);
        assertTrue("Sheep is Animal", EntityGroupingUtil.isAnimal(sheep));
        assertFalse("Zombie is not Animal", EntityGroupingUtil.isAnimal(zombie));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsAnimalEntityType() {
        assertTrue("Sheep is Animal", EntityGroupingUtil.isAnimal(EntityType.SHEEP));
        assertFalse("Zombie is not Animal", EntityGroupingUtil.isAnimal(EntityType.ZOMBIE));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsAnimalClassOfQextendsEntity() {
        assertTrue("Sheep is Animal", EntityGroupingUtil.isAnimal(Sheep.class));
        assertFalse("Zombie is not Animal", EntityGroupingUtil.isAnimal(Zombie.class));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsMonsterString() {
        assertTrue("Zombie is Monster", EntityGroupingUtil.isMonster("zombie"));
        assertFalse("Sheep is not Monster", EntityGroupingUtil.isMonster("sheep"));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsMonsterEntity() {
        Entity sheep = generateEntity(Sheep.class);
        Entity zombie = generateEntity(Zombie.class);
        assertTrue("Zombie is Monster", EntityGroupingUtil.isMonster(zombie));
        assertFalse("Sheep is not Monster", EntityGroupingUtil.isMonster(sheep));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsMonsterEntityType() {
        assertTrue("Zombie is Monster", EntityGroupingUtil.isMonster(EntityType.ZOMBIE));
        assertFalse("Sheep is not Monster", EntityGroupingUtil.isMonster(EntityType.SHEEP));
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsMonsterClassOfQextendsEntity() {
        assertTrue("Zombie is Monster", EntityGroupingUtil.isMonster(Zombie.class));
        assertFalse("Sheep is not Monster", EntityGroupingUtil.isMonster(Sheep.class));
    }

    @Test
    public void testGetNameClassOfQextendsEntity() {
        assertEquals("Zombie.class = zombie", "zombie", EntityGroupingUtil.getName(Zombie.class));
    }

    @Test
    public void testGetNameEntityType() {
        assertEquals("Zombie type = zombie", "zombie", EntityGroupingUtil.getName(EntityType.ZOMBIE));
    }

    public static enum EntityTestList {
        ANIMAL_TEST(EntityCategory.ANIMAL, EntityType.SHEEP, EntityType.ZOMBIE, true),
        MONSTER_TEST(EntityCategory.MONSTER, EntityType.ZOMBIE, EntityType.SHEEP, true),
        NPC_TEST(EntityCategory.NPC, EntityType.VILLAGER, EntityType.ZOMBIE, true),
        PASSIVE_TEST(EntityCategory.PASSIVE, EntityType.SHEEP, EntityType.ZOMBIE, false),
        HOSTILE_TEST(EntityCategory.HOSTILE, EntityType.ZOMBIE, EntityType.SHEEP, false),
        UTILITY_TEST(EntityCategory.UTILITY, EntityType.IRON_GOLEM, EntityType.SHEEP, false),
        BOSS_TEST(EntityCategory.BOSS, EntityType.WITHER, EntityType.SHEEP, false),
        TAMEABLE_TEST(EntityCategory.TAMEABLE, EntityType.OCELOT, EntityType.SHEEP, false);

        private EntityCategory entityCategory;
        private EntityType matching;
        private EntityType nonMatching;
        private boolean legacyField;
        private EntityTestList (EntityCategory entityCategory, EntityType matching, EntityType nonMatching, boolean legacyField) {
            this.setEntityCategory(entityCategory);
            this.setLegacyField(legacyField);
            this.setMatching(matching);
            this.setNonMatching(nonMatching);
        }
        public EntityCategory getEntityCategory() {
            return entityCategory;
        }
        public void setEntityCategory(EntityCategory entityCategory) {
            this.entityCategory = entityCategory;
        }
        public EntityType getMatching() {
            return matching;
        }
        public void setMatching(EntityType matching) {
            this.matching = matching;
        }
        public EntityType getNonMatching() {
            return nonMatching;
        }
        public void setNonMatching(EntityType nonMatching) {
            this.nonMatching = nonMatching;
        }
        public boolean isLegacyField() {
            return legacyField;
        }
        public void setLegacyField(boolean legacyField) {
            this.legacyField = legacyField;
        }


    }

}

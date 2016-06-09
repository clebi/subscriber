// Copyright 2016 Clément Bizeau
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.clebi.subscribers.daos;

import org.clebi.subscribers.daos.elasticsearch.SubscriberDaoImpl;
import org.clebi.subscribers.daos.exceptions.ValidationException;
import org.clebi.subscribers.model.Email;
import org.clebi.subscribers.model.Subscriber;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;

public class SubsciberDaoTest {

  private static final String TEST_PROJECT = "test_unit_project";

  @Test(expected = ValidationException.class)
  public void testAddNotValidSubscriber() throws Exception {
    SubscriberDao dao = new SubscriberDaoImpl(() -> null);
    dao.addSubscriber(
        TEST_PROJECT,
        new Subscriber(
            true,
            true,
            new Email("bad_email"),
            ZonedDateTime.now(ZoneOffset.UTC),
            new HashMap<String, Object>()
        )
    );
  }
}

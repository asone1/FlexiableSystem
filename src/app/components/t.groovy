#!

import de.css.lims.auftrag.Laborauftrag
import de.integris.kit.adm.Team
import de.integris.kit.adm.Todo
import de.integris.kit.adm.TodoCategory
import de.integris.kit.adm.TodoSupport
import de.integris.kit.adm.todo.TodoGenerator
import de.integris.kit.bas.IArray
import de.integris.kit.bas.MutableArray
import de.integris.kit.ctl.*
import java.text.SimpleDateFormat
import de.css.lims.basisklassen.Logic.Limslogic
import de.integris.kit.bas.IArray
import de.integris.kit.ctl.EditingContext
import de.integris.kit.mail.MailLogic
import de.integris.kit.mail.MailParameter

Laborauftrag obj = (Laborauftrag) binding.variables.get(Limslogic.GROOVY_VARS_AKTUELLESOBJEKT)
EditingContext context = (EditingContext) binding.variables.get(Limslogic.GROOVY_VARS_EDITING_CONTEXT)
PropertyEdit edit = context.getCurrentEdit()?: context.beginEdit(null)

//report team
String teamNr = "0009"
String todoCategoryNr = "0"
Date sentDate = new Date()
Calendar c = Calendar.getInstance() 
TodoCategory todoCategory = null
Team team = null
String processingComplete = '2'
String subject ="Please create report for Order " + obj.nr +".";

FetchSpecification teamFs = new FetchSpecification(Team.class, new KeyValueQualifier(Team.NO, Qualifier.EQUAL, teamNr))
IArray<Team> teamArray = context.fetchObjects(teamFs)
if (teamArray != null && !teamArray.isEmpty()) {
    team = teamArray.firstObject()
           context.info('Found team: ' + team.getName())
}

// Getting the todoCategory object
FetchSpecification todoCategoryFs = new FetchSpecification(TodoCategory.class, new KeyValueQualifier(TodoCategory.NR, Qualifier.EQUAL, todoCategoryNr))
IArray<TodoCategory> todoCategoryArray = context.fetchObjects(todoCategoryFs)
if (todoCategoryArray != null && !todoCategoryArray.isEmpty()) {
    todoCategory = todoCategoryArray.firstObject()
                   context.info('Found TODO category: ' + todoCategory.getName())
}

// Getting all todoObjects for the Food Experts todoCategory and the user
AndQualifier todoQualifier = new AndQualifier()
todoQualifier.addToQualifiers(new KeyObjectQualifier(Todo.TEAM, Qualifier.EQUAL, team))
todoQualifier.addToQualifiers(new KeyObjectQualifier(Todo.CATEGORY, Qualifier.EQUAL, todoCategory))
FetchSpecification todoFs = new FetchSpecification(Todo.class, todoQualifier)


MutableArray references = new MutableArray<TodoSupport>()
references.addObject(obj)
TodoGenerator.generateTodo(edit, null, team, subject, subject, false, false,references, false, todoCategory)

context.warning("todo sent")

MailParameter mailParameter = new MailParameter()
mailParameter.message = subject + "<br><br>Best regards,<br>Microlab Laboratories (99) Ltd<br>Oppenheimer 5, Rabin Park<br>Rehovot 76701<br>Cell phone: 054-8381718<br>Phone: 08-9362280<br>Fax: 08-9361247<br>";
mailParameter.from = "Asia.lims@tentamus.com"
mailParameter.to = "tina.yu@tentamus.com"
mailParameter.subject = "test"
mailParameter.contentType = "text/html"


// Send e-mail
MailLogic mailLogic = MailLogic.getMailLogic(context)
mailLogic.setEditingContext(context)
mailLogic.sendMessage(mailParameter)


context.warning('mail sent')
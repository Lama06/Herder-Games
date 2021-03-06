using System.Collections;
using UnityEngine;

namespace HerderGames.Lehrer.AI
{
    public abstract class GoalBase
    {
        public Lehrer Lehrer { get; }
        private Coroutine ExecuteCoroutine;

        protected GoalBase(Lehrer lehrer)
        {
            Lehrer = lehrer;
        }

        public virtual void OnGoalEnable()
        {
        }

        public abstract bool ShouldRun(bool currentlyRunning);

        public void StartGoal()
        {
            ExecuteCoroutine = Lehrer.AI.StartCoroutine(Execute());
            OnGoalStart();
        }

        protected virtual void OnGoalStart()
        {
        }

        public void EndGoal()
        {
            OnGoalEnd();
            Lehrer.AI.StopCoroutine(ExecuteCoroutine);
            Lehrer.Sprache.SaetzeMoeglichkeiten = null;
            Lehrer.Agent.destination = Lehrer.transform.position;
        }

        protected virtual void OnGoalEnd()
        {
        }

        protected virtual IEnumerator Execute()
        {
            yield return null;
        }
    }
}
